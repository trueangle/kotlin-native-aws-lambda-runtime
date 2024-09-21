package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.api.LambdaClientImpl
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.github.trueangle.knative.lambda.runtime.log.KtorLogger
import io.github.trueangle.knative.lambda.runtime.log.LambdaLogger
import io.github.trueangle.knative.lambda.runtime.log.Log
import io.github.trueangle.knative.lambda.runtime.log.debug
import io.github.trueangle.knative.lambda.runtime.log.error
import io.github.trueangle.knative.lambda.runtime.log.fatal
import io.github.trueangle.knative.lambda.runtime.log.info
import io.github.trueangle.knative.lambda.runtime.log.warn
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.curl.Curl
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.content.OutgoingContent.WriteChannelContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object LambdaRuntime {
    @OptIn(ExperimentalSerializationApi::class)
    internal val json = Json { explicitNulls = false }

    @PublishedApi
    internal val curlHttpClient = createHttpClient(Curl.create())

    inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) = runBlocking {
        val lambdaClient = LambdaClientImpl(curlHttpClient)

        Runner(client = lambdaClient, log = Log).run(false, initHandler)
    }

    @PublishedApi
    internal fun createHttpClient(engine: HttpClientEngine) = HttpClient(engine) {
        install(HttpTimeout)
        install(ContentNegotiation) { json(json) }
        install(Logging) {
            val kLogger = KtorLogger()
            level = kLogger.getLevel()
            logger = kLogger

            filter { !it.headers.contains("Lambda-Runtime-Function-Response-Mode", "streaming") }
        }
    }
}

@PublishedApi
internal class Runner(
    val client: LambdaClient,
    val log: LambdaLogger,
    val env: LambdaEnvironment = LambdaEnvironment()
) {
    suspend inline fun <reified I, reified O> run(singleEventMode: Boolean = false, crossinline initHandler: () -> LambdaHandler<I, O>) {
        val handler = try {
            log.info("Initializing Kotlin Native Lambda Runtime")

            initHandler()
        } catch (e: Exception) {
            log.fatal(e)

            client.reportError(e.asInitError())

            env.terminate()
        }

        val handlerName = handler::class.simpleName
        val inputTypeInfo = typeInfo<I>()
        val outputTypeInfo = typeInfo<O>()

        var shouldExit = false
        while (!shouldExit) {
            try {
                log.info("Runtime is ready for a new event")

                try {
                    val (event, context) = client.retrieveNextEvent<I>(inputTypeInfo)

                    with(log) {
                        setContext(context)

                        debug(event)
                        debug(context)
                        info("$handlerName invocation started")
                    }

                    if (handler is LambdaStreamHandler<I, *>) {
                        val response = streamingResponse { handler.handleRequest(event, it, context) }

                        log.info("$handlerName started response streaming")

                        client.streamResponse(context, response)
                    } else {
                        handler as LambdaBufferedHandler<I, O>
                        val response = bufferedResponse(context) { handler.handleRequest(event, context) }

                        log.info("$handlerName invocation completed")
                        log.debug(response)

                        client.sendResponse(context, response, outputTypeInfo)
                    }
                } catch (e: LambdaRuntimeException) {
                    log.error(e)

                    client.reportError(e)
                }
            } catch (e: LambdaEnvironmentException) {
                when (e) {
                    is NonRecoverableStateException -> {
                        log.fatal(e)

                        env.terminate()
                    }

                    else -> log.error(e)
                }
            } catch (e: Throwable) {
                log.fatal(e)

                env.terminate()
            }

            if (singleEventMode) {
                shouldExit = singleEventMode
            }
        }
    }

    inline fun streamingResponse(crossinline handler: suspend (ByteWriteChannel) -> Unit) = object : WriteChannelContent() {
        override suspend fun writeTo(channel: ByteWriteChannel) {
            try {
                handler(channel)
            } catch (e: Exception) {
                log.warn("Exception occurred on streaming: " + e.message)
                log.error(e)

                channel.writeMidstreamError(e)
            }
        }
    }

    inline fun <T, R> T.bufferedResponse(context: Context, block: T.() -> R): R = try {
        block()
    } catch (e: Exception) {
        throw e.asHandlerError(context)
    }
}

suspend fun ByteWriteChannel.writeMidstreamError(e: Throwable) = writeStringUtf8(e.toTrailer())

internal fun Throwable.toTrailer(): String =
    "Lambda-Runtime-Function-Error-Type: Runtime.StreamError\r\nLambda-Runtime-Function-Error-Body: ${stackTraceToString().encodeBase64()}\r\n"
package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.github.trueangle.knative.lambda.runtime.log.KtorLogger
import io.github.trueangle.knative.lambda.runtime.log.Log
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
import kotlin.system.exitProcess

object LambdaRuntime {
    @OptIn(ExperimentalSerializationApi::class)
    internal val json = Json { explicitNulls = false }

    inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) = runBlocking {
        val curlHttpClient = createHttpClient(Curl.create())
        val lambdaClient = LambdaClient(curlHttpClient)

        Runner(lambdaClient).run(initHandler)
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
) {
    suspend inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) {
        val handler = try {
            Log.info("Initializing Kotlin Native Lambda Runtime")

            initHandler()
        } catch (e: Exception) {
            Log.fatal(e)

            client.reportError(e.asInitError())
            exitProcess(1)
        }

        val handlerName = handler::class.simpleName
        val inputTypeInfo = typeInfo<I>()
        val outputTypeInfo = typeInfo<O>()

        while (true) {
            try {
                Log.info("Runtime is ready for a new event")

                val (event, context) = client.retrieveNextEvent<I>(inputTypeInfo)

                with(Log) {
                    setContext(context)

                    debug(event)
                    debug(context)
                }

                Log.info("$handlerName invocation started")

                if (handler is LambdaStreamHandler<I, *>) {
                    val response = streamingResponse { handler.handleRequest(event, it, context) }

                    Log.info("$handlerName started response streaming")

                    client.streamResponse(context, response)
                } else {
                    handler as LambdaBufferedHandler<I, O>
                    val response = bufferedResponse(context) { handler.handleRequest(event, context) }

                    Log.info("$handlerName invocation completed")
                    Log.debug(response)

                    client.sendResponse(context, response, outputTypeInfo)
                }
            } catch (e: LambdaRuntimeException) {
                Log.error(e)

                client.reportError(e)
            } catch (e: LambdaEnvironmentException) {
                when (e) {
                    is NonRecoverableStateException -> {
                        Log.fatal(e)

                        exitProcess(1)
                    }

                    else -> Log.error(e)
                }
            } catch (e: Throwable) {
                Log.fatal(e)

                exitProcess(1)
            }
        }
    }

    inline fun streamingResponse(crossinline handler: suspend (ByteWriteChannel) -> Unit) = object : WriteChannelContent() {
        override suspend fun writeTo(channel: ByteWriteChannel) {
            try {
                handler(channel)
            } catch (e: Exception) {
                Log.warn("Exception occurred on streaming: " + e.message)

                channel.writeStringUtf8(e.toTrailer())
            }
        }

        private fun Throwable.toTrailer(): String =
            "Lambda-Runtime-Function-Error-Type: Runtime.StreamError\r\nLambda-Runtime-Function-Error-Body: ${stackTraceToString().encodeBase64()}\r\n"
    }

    inline fun <T, R> T.bufferedResponse(context: Context, block: T.() -> R): R = try {
        block()
    } catch (e: Exception) {
        throw e.asHandlerError(context)
    }
}
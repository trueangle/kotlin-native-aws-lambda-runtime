package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.github.trueangle.knative.lambda.runtime.log.JsonLogFormatter
import io.github.trueangle.knative.lambda.runtime.log.LambdaLogger
import io.github.trueangle.knative.lambda.runtime.log.LambdaLoggerImpl
import io.github.trueangle.knative.lambda.runtime.log.StdoutLogWriter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.content.OutgoingContent.WriteChannelContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

object LambdaRuntime {
    inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) = runBlocking {
        val httpClient = HttpClient(CIO) {
            install(HttpTimeout)
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                //level = LogLevel.ALL
            }
        }
        val client = LambdaClient(httpClient)
        val logger = LambdaLoggerImpl(StdoutLogWriter(), JsonLogFormatter())

        val handler = try {
            initHandler()
        } catch (e: Exception) {
            logger.fatal(e)

            client.reportError(e.asInitError())
            exitProcess(1)
        }

        val inputTypeInfo = typeInfo<I>()
        val outputTypeInfo = typeInfo<O>()

        while (true) {
            try {
                val (event, context) = client.retrieveNextEvent<I>(inputTypeInfo)

                with(logger) {
                    setContext(context)
                    trace(event)
                    trace(context)
                }

                if (handler is LambdaStreamHandler<I, *>) {
                    val response = streamingResponse(logger) { handler.handleRequest(event, it, context) }
                    client.streamResponse(context, response)
                } else {
                    handler as LambdaBufferedHandler<I, O>

                    val response = bufferedResponse(context) { handler.handleRequest(event, context) }
                    client.sendResponse(context, response, outputTypeInfo)
                }
            } catch (e: LambdaRuntimeException) {
                logger.error(e)
                client.reportError(e)
            } catch (e: LambdaEnvironmentException) {
                when (e) {
                    is NonRecoverableStateException -> {
                        logger.fatal(e)
                        exitProcess(1)
                    }

                    else -> logger.error(e)
                }
            } catch (e: Throwable) {
                logger.fatal(e)

                exitProcess(1)
            }
        }
    }
}

@PublishedApi
internal inline fun streamingResponse(logger: LambdaLogger, crossinline handler: suspend (ByteWriteChannel) -> Unit) =
    object : WriteChannelContent() {
        override suspend fun writeTo(channel: ByteWriteChannel) {
            try {
                handler(channel)
            } catch (e: Exception) {
                logger.warn(e)
                channel.writeStringUtf8(e.toTrailer())
            }
        }

        private fun Throwable.toTrailer(): String =
            "Lambda-Runtime-Function-Error-Type: Runtime.StreamError\r\nLambda-Runtime-Function-Error-Body: ${stackTraceToString().encodeBase64()}\r\n"
    }

@PublishedApi
internal inline fun <T, R> T.bufferedResponse(context: Context, block: T.() -> R): R = try {
    block()
} catch (e: Exception) {
    throw e.asHandlerError(context)
}
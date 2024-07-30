package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.github.trueangle.knative.lambda.runtime.log.Log
import io.github.trueangle.knative.lambda.runtime.log.LogLevel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel as KtorLogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.content.OutgoingContent.WriteChannelContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.system.exitProcess

object LambdaRuntime {
    private val httpClient = HttpClient(CIO) {
        install(HttpTimeout)
        install(ContentNegotiation) {
            json(Json { explicitNulls = false })
        }
        install(Logging) {
            level = if (Log.currentLogLevel == LogLevel.TRACE) KtorLogLevel.ALL else KtorLogLevel.NONE
            logger = object : Logger {
                override fun log(message: String) = Log.trace(message)
            }
            filter { it.headers.contains("Lambda-Runtime-Function-Response-Mode") }
        }
    }

    @PublishedApi
    internal val client = LambdaClient(httpClient)

    inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) = runBlocking {
        val handler = try {
            initHandler()
        } catch (e: Exception) {
            Log.fatal(e)

            client.reportError(e.asInitError())
            exitProcess(1)
        }

        val inputTypeInfo = typeInfo<I>()
        val outputTypeInfo = typeInfo<O>()

        while (true) {
            try {
                val (event, context) = client.retrieveNextEvent<I>(inputTypeInfo)

                with(Log) {
                    setContext(context)
                    trace(event)
                    trace(context)
                }

                if (handler is LambdaStreamHandler<I, *>) {
                    val response = streamingResponse { handler.handleRequest(event, it, context) }
                    client.streamResponse(context, response)
                } else {
                    handler as LambdaBufferedHandler<I, O>
                    val response = bufferedResponse(context) { handler.handleRequest(event, context) }
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
}

@PublishedApi
internal inline fun streamingResponse(crossinline handler: suspend (ByteWriteChannel) -> Unit) =
    object : WriteChannelContent() {
        override suspend fun writeTo(channel: ByteWriteChannel) {
            try {
                handler(channel)
            } catch (e: Exception) {
                Log.warn(e)
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
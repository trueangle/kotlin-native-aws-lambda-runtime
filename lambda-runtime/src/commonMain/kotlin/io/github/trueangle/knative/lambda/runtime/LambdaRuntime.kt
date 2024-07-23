package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
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
                level = LogLevel.ALL
            }
        }
        val client = LambdaClient(httpClient)

        val handler = try {
            initHandler()
        } catch (e: Exception) {
            e.printStackTrace()

            client.reportError(e.asInitError())
            exitProcess(1)
        }

        val inputTypeInfo = typeInfo<I>()
        val outputTypeInfo = typeInfo<O>()

        while (true) {
            try {
                val event = client.retrieveNextEvent<I>(inputTypeInfo)

                if (handler is LambdaStreamHandler<I, *>) {
                    val response = streamingResponse { handler.handleRequest(event.body, it, event.context) }
                    client.streamResponse(event.context, response)
                } else {
                    handler as LambdaBufferedHandler<I, O>

                    val response = bufferedResponse(event.context) { handler.handleRequest(event.body, event.context) }
                    client.sendResponse(event.context, response, outputTypeInfo)
                }
            } catch (e: LambdaRuntimeException) {
                client.reportError(e)
            } catch (e: LambdaEnvironmentException) {
                e.printStackTrace()
                when (e) {
                    is LambdaEnvironmentException.NonRecoverableStateException -> exitProcess(1)
                    else -> Unit
                }
            } catch (e: Throwable) {
                e.printStackTrace()

                exitProcess(1)
            }
        }
    }
}

@PublishedApi
internal inline fun streamingResponse(crossinline handler: suspend (ByteWriteChannel) -> Unit) = object : WriteChannelContent() {
    override suspend fun writeTo(channel: ByteWriteChannel) {
        try {
            handler(channel)
        } catch (e: Exception) {
            channel.writeStringUtf8(e.toTrailer())
        }
    }

    private fun Throwable.toTrailer(): String =
        "Lambda-Runtime-Function-Error-Type: Runtime.StreamError\r\nLambda-Runtime-Function-Error-Body: ${stackTraceToString().encodeBase64()}\r\n"
}

@PublishedApi
internal inline fun <T, R> T.bufferedResponse(context: Context, block: T.() -> R): R {
    return try {
        block()
    } catch (e: Exception) {
        throw e.asHandlerError(context)
    }
}
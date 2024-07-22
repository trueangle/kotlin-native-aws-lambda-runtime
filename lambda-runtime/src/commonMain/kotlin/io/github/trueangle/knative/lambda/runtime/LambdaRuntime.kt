package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.BodyParseException
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.api.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.api.asEventBodyParseError
import io.github.trueangle.knative.lambda.runtime.api.asHandlerError
import io.github.trueangle.knative.lambda.runtime.api.asInitError
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
    @Suppress("UNCHECKED_CAST")
    inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) = runBlocking {
        println("Start runtime")

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

            client.sendError(e.asInitError())
            exitProcess(1)
        }

        val inputTypeInfo = typeInfo<I>()
        val outputTypeInfo = typeInfo<O>()

        // todo refactor error handle
        try {
            while (true) {
                val event = try {
                    client.retrieveNextEvent<I>(inputTypeInfo)
                } catch (e: BodyParseException) {
                    client.sendError(e.asEventBodyParseError())

                    break
                } catch (e: Exception) {
                    e.printStackTrace()

                    client.sendError(e.asInitError())
                    exitProcess(1)
                }

                try {
                    if (handler is LambdaStreamHandler<I, *>) {
                        val response = streamingResponse { handler.handleRequest(event.body, it, event.context) }
                        client.streamResponse(event.context, response)
                    } else {
                        val response = (handler as LambdaBufferedHandler<I, O>).handleRequest(event.body, event.context)
                        client.sendResponse(event.context, response, outputTypeInfo)
                    }
                } catch (e: NonRecoverableStateException) {
                    throw e
                } catch (e: Exception) {
                    e.printStackTrace()

                    client.sendError(e.asHandlerError(event.context))

                    break
                }
            }
        } catch (e: NonRecoverableStateException) {
            e.printStackTrace()
            exitProcess(1)
        } finally {
            httpClient.close()
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
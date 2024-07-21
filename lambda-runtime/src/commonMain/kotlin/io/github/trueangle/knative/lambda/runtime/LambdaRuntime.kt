package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.BodyParseException
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.api.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.api.asEventBodyParseError
import io.github.trueangle.knative.lambda.runtime.api.asHandlerError
import io.github.trueangle.knative.lambda.runtime.api.asInitError
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

object LambdaRuntime {
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
                    if (handler is LambdaStreamHandler) {
                        val result = handler.handleRequest(event.body, event.context)
                        client.streamResponse(event.context, result)
                    } else {
                        val result = handler.handleRequest(event.body, event.context)
                        client.sendResponse(event.context, result, outputTypeInfo)
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
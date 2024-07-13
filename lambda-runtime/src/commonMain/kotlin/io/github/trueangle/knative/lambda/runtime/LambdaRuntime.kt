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
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

object LambdaRuntime {
    @Suppress("UNCHECKED_CAST")
    inline fun <reified I, reified O> run(crossinline initHandler: () -> LambdaHandler<I, O>) = runBlocking {
        val httpClient = HttpClient(CIO) {
            install(HttpTimeout)
            install(ContentNegotiation) {
                json()
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
                        val resultStream = handler.handleRequest(event.body, event.context)
                        client.streamResponse(event.context, resultStream as Flow<String>)
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
package io.github.trueangle.knative.lambda.runtime

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.runBlocking

object LambdaRuntime {
    fun run(handler: LambdaHandler) = runBlocking {
        println("Start event loop")

        val httpClient = HttpClient(CIO) {
            install(HttpTimeout)
        }

        val client = LambdaClient(httpClient)
        println("Client configured")

        while (true) {
            val event = client.retrieveNextEvent()
            try {
                try {
                    val result = handler.handleRequest(event.payload, event.context)
                    client.sendResponse(event.context, result)
                } catch (e: Exception) {
                    e.printStackTrace()

                    client.sendLambdaInvocationError(
                        event.context,
                        "Runtime.UnknownReason",
                        e.message ?: e.stackTraceToString()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()

                client.sendRuntimeError(
                    event.context,
                    "Runtime.UnknownReason",
                    e.message ?: e.stackTraceToString()
                )
                break
            }
        }

        httpClient.close()
    }
}
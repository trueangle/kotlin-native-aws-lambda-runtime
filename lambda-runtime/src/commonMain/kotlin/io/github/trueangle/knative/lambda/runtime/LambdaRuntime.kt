package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.api.toInitInvocationError
import io.github.trueangle.knative.lambda.runtime.api.toUnknownInvocationError
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import kotlinx.coroutines.runBlocking

object LambdaRuntime {
    fun run() = runBlocking {
        val httpClient = HttpClient(CIO) { install(HttpTimeout) }
        val client = LambdaClient(httpClient)

        try {
            val handler = SampleLambda()

            while (true) {
                val event = client.retrieveNextEvent()
                try {
                    // todo payload to actual types
                    val result = handler.handleRequest(event.payload, event.context)
                    client.sendResponse(event.context, result)
                } catch (e: Exception) {
                    client.sendError(e.toUnknownInvocationError(event.context))

                    break
                }
            }
        } catch (e: Exception) {
            client.sendError(e.toInitInvocationError())
        } finally {
            httpClient.close()
        }
    }
}
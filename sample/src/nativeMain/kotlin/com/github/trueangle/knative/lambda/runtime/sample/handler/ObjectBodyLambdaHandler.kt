package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import kotlinx.serialization.Serializable

class ObjectBodyLambdaHandler : LambdaHandler<Request, Response> {
    override fun handleRequest(input: Request, context: Context): Response {
        println("Invoke lambda handler\n payload: $input\n context: $context")

        return Response(body = input.question + "\n answer is Hello world")
    }
}

@Serializable
data class Request(val question: String)
@Serializable
data class Response(
    val statusCode: Int = 200,
    val headers: Map<String, String> = mapOf("Content-Type" to "application/json"),
    val isBase64Encoded: Boolean = false,
    val body: String
)
package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.events.apigateway.APIGatewayProxy
import io.github.trueangle.knative.lambda.runtime.events.apigateway.APIGatewayV2Request
import io.github.trueangle.knative.lambda.runtime.events.apigateway.APIGatewayV2Response
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.log.Log
import kotlinx.serialization.Serializable

class ObjectBodyLambdaHandler : LambdaBufferedHandler<APIGatewayV2Request, APIGatewayV2Response> {
    override suspend fun handleRequest(input: APIGatewayV2Request, context: Context): APIGatewayV2Response {
        /*  Log.info(input)
          Log.info(context)
          Log.fatal(RuntimeException())*/
        //Log.info(input.question + "\n answer is Hello world")

        return APIGatewayV2Response(
            statusCode = 200,
            body = "\n answer is Hello world",
            cookies = null,
            headers = null,
            isBase64Encoded = false
        )
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
package io.github.trueangle.knative.lambda.runtime.events.apigateway

import com.goncalossilva.resources.Resource
import kotlinx.serialization.json.Json
import kotlin.test.Test

const val RESOURCES_PATH = "src/nativeTest/resources"

class APIGatewayTest {

    @Test
    fun `WHEN APIGatewayRequest json THEN parse`() {
        val jsonString = Resource("$RESOURCES_PATH/example-apigw-request.json").readText()
        Json.decodeFromString<APIGatewayRequest>(jsonString)
    }

    @Test
    fun `WHEN APIGatewayCustomAuthorizer json THEN parse`() {
        val jsonString = Resource("$RESOURCES_PATH/example-apigw-v2-custom-authorizer-v1-request.json").readText()
        Json.decodeFromString<APIGatewayCustomAuthorizer>(jsonString)
    }

    @Test
    fun `WHEN APIGatewayProxy json THEN parse`() {
        // https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format
        val jsonString = Resource("$RESOURCES_PATH/example-apigw-proxy-request.json").readText()
        Json.decodeFromString<APIGatewayProxy>(jsonString)
    }
}
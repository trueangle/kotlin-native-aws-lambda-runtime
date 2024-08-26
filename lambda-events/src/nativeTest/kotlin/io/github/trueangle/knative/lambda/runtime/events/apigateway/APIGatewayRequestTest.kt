package io.github.trueangle.knative.lambda.runtime.events.apigateway

import com.goncalossilva.resources.Resource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

const val RESOURCES_PATH = "src/nativeTest/resources"

class APIGatewayRequestTest {

    @Test
    fun `GIVEN APIGatewayRequest json AND object body THEN parse`() {
        val jsonString = Resource("$RESOURCES_PATH/example-apigw-request.json").readText()
        Json.decodeFromString<APIGatewayRequest>(jsonString)
    }
}
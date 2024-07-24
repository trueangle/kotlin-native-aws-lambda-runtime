package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class LambdaGatewayProxyEvent(
    @SerialName("resource") val resource: String,
    @SerialName("path") val path: String,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("stageVariables") val stageVariables: Map<String, String>? = null,
    @SerialName("cookies") val cookies: List<String>? = null,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>? = null,
    @SerialName("pathParameters") val pathParameters: Map<String, String>? = null,
    @SerialName("requestContext") val requestContext: RequestContext,
    @SerialName("body") val body: String? = null,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
) {
    @Serializable
    data class RequestContext(
        @SerialName("accountId") val accountID: String,
        @SerialName("apiId") val apiID: String,
        @SerialName("domainName") val domainName: String,
        @SerialName("domainPrefix") val domainPrefix: String,
        @SerialName("stage") val stage: String,
        @SerialName("requestId") val requestID: String,
        @SerialName("httpMethod") val httpMethod: String,
        @SerialName("authorizer") val authorizer: Authorizer? = null,
        @SerialName("resourcePath") val resourcePath: String? = null,
        @SerialName("path") val path: String? = null,
        @SerialName("requestTime") val requestTime: String? = null,
        @SerialName("requestTimeEpoch") val requestTimeEpoch: ULong
    ) {

        @Serializable
        data class Authorizer(
            @SerialName("claims") val claims: Map<String, String>? = null,
            @SerialName("scopes") val scopes: List<String>? = null
        )
    }
}
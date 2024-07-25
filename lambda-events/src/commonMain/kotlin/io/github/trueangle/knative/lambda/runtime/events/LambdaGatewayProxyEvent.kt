package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class LambdaGatewayProxyEvent<T>(
    @SerialName("resource") val resource: String,
    @SerialName("path") val path: String,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("stageVariables") val stageVariables: Map<String, String>?,
    @SerialName("cookies") val cookies: List<String>?,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>?,
    @SerialName("pathParameters") val pathParameters: Map<String, String>?,
    @SerialName("requestContext") val requestContext: RequestContext,
    @SerialName("body") val body: T?,
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
        @SerialName("authorizer") val authorizer: Authorizer?,
        @SerialName("resourcePath") val resourcePath: String?,
        @SerialName("path") val path: String?,
        @SerialName("requestTime") val requestTime: String?,
        @SerialName("requestTimeEpoch") val requestTimeEpoch: ULong
    ) {

        @Serializable
        data class Authorizer(
            @SerialName("claims") val claims: Map<String, String>?,
            @SerialName("scopes") val scopes: List<String>?
        )
    }
}
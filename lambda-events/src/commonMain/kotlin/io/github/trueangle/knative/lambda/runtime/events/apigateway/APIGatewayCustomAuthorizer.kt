package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIGatewayCustomAuthorizer(
    @SerialName("version") val version: String?,
    @SerialName("type") val type: String?,
    @SerialName("methodArn") val methodArn: String?,
    @SerialName("identitySource") val identitySource: String?,
    @SerialName("authorizationToken") val authorizationToken: String?,
    @SerialName("resource") val resource: String?,
    @SerialName("path") val path: String?,
    @SerialName("httpMethod") val httpMethod: String?,
    @SerialName("headers") val headers: Map<String, String>?,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>?,
    @SerialName("pathParameters") val pathParameters: Map<String, String>?,
    @SerialName("stageVariables") val stageVariables: Map<String, String>?,
    @SerialName("requestContext") val requestContext: RequestContext?
) {
    @Serializable
    data class RequestContext(
        @SerialName("path") val path: String?,
        @SerialName("accountId") val accountId: String?,
        @SerialName("resourceId") val resourceId: String?,
        @SerialName("stage") val stage: String?,
        @SerialName("requestId") val requestId: String?,
        @SerialName("identity") val identity: Identity?,
        @SerialName("resourcePath") val resourcePath: String?,
        @SerialName("httpMethod") val httpMethod: String?,
        @SerialName("apiId") val apiId: String?
    )

    @Serializable
    data class Identity(
        @SerialName("apiKey") val apiKey: String?,
        @SerialName("sourceIp") val sourceIp: String?
    )
}

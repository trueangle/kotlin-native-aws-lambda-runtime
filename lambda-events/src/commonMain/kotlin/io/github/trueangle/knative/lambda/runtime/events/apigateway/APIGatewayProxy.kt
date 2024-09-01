package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class APIGatewayProxy(
    @SerialName("version") val version: String,
    @SerialName("resource") val resource: String?,
    @SerialName("path") val path: String?,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("headers") val headers: Map<String, String>?,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>>?,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>?,
    @SerialName("multiValueQueryStringParameters") val multiValueQueryStringParameters: Map<String, List<String>>?,
    @SerialName("pathParameters") val pathParameters: Map<String, String>?,
    @SerialName("stageVariables") val stageVariables: Map<String, String>?,
    @SerialName("requestContext") val requestContext: ProxyRequestContext,
    @SerialName("body") val body: String?,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
) {
    @Serializable
    data class ProxyRequestContext(
        @SerialName("accountId") val accountId: String?,
        @SerialName("stage") val stage: String?,
        @SerialName("resourceId") val resourceId: String?,
        @SerialName("requestId") val requestId: String?,
        @SerialName("operationName") val operationName: String?,
        @SerialName("identity") val identity: RequestIdentity?,
        @SerialName("resourcePath") val resourcePath: String?,
        @SerialName("httpMethod") val httpMethod: String,
        @SerialName("apiId") val apiId: String?,
        @SerialName("path") val path: String?,
        @SerialName("authorizer") val authorizer: Map<String, Map<String, String>>?,
        @SerialName("extendedRequestId") val extendedRequestId: String?,
        @SerialName("requestTime") val requestTime: String?,
        @SerialName("requestTimeEpoch") val requestTimeEpoch: Long,
        @SerialName("domainName") val domainName: String?,
        @SerialName("domainPrefix") val domainPrefix: String?,
        @SerialName("protocol") val protocol: String?
    )

    @Serializable
    data class RequestIdentity(
        @SerialName("cognitoIdentityPoolId") val cognitoIdentityPoolId: String?,
        @SerialName("accountId") val accountId: String?,
        @SerialName("cognitoIdentityId") val cognitoIdentityId: String?,
        @SerialName("caller") val caller: String?,
        @SerialName("apiKey") val apiKey: String?,
        @SerialName("principalOrgId") val principalOrgId: String?,
        @SerialName("sourceIp") val sourceIp: String?,
        @SerialName("cognitoAuthenticationType") val cognitoAuthenticationType: String?,
        @SerialName("cognitoAuthenticationProvider") val cognitoAuthenticationProvider: String?,
        @SerialName("userArn") val userArn: String?,
        @SerialName("userAgent") val userAgent: String?,
        @SerialName("user") val user: String?,
        @SerialName("accessKey") val accessKey: String?
    )
}
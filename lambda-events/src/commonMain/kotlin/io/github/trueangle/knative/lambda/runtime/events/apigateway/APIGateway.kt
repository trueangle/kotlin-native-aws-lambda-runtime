package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIGatewayRequest(
    @SerialName("resource") val resource: String,
    @SerialName("path") val path: String,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>? = null,
    @SerialName("multiValueQueryStringParameters") val multiValueQueryStringParameters: Map<String, List<String>>? = null,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, List<String>>,
    @SerialName("pathParameters") val pathParameters: Map<String, String>? = null,
    @SerialName("stageVariables") val stageVariables: Map<String, String>? = null,
    @SerialName("requestContext") val requestContext: Context,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean? = null
) {
    @Serializable
    data class Context(
        @SerialName("resourceId") val resourceId: String,
        @SerialName("apiId") val apiId: String,
        @SerialName("domainName") val domainName: String,
        @SerialName("domainPrefix") val domainPrefix: String,
        @SerialName("resourcePath") val resourcePath: String,
        @SerialName("protocol") val protocol: String,
        @SerialName("httpMethod") val httpMethod: String,
        @SerialName("requestId") val requestId: String,
        @SerialName("accountId") val accountId: String,
        @SerialName("stage") val stage: String,
        @SerialName("identity") val identity: Identity,
        @SerialName("authorizer") val authorizer: Authorizer? = null,
        @SerialName("extendedRequestId") val extendedRequestId: String? = null,
        @SerialName("path") val path: String,
        @SerialName("requestTime") val requestTime: String,
        @SerialName("requestTimeEpoch") val requestTimeEpoch: Long,
    ) {
        @Serializable
        data class Identity(
            @SerialName("cognitoIdentityPoolId") val cognitoIdentityPoolId: String? = null,
            @SerialName("apiKey") val apiKey: String? = null,
            @SerialName("apiKeyId") val apiKeyId: String? = null,
            @SerialName("accessKey") val accessKey: String? = null,
            @SerialName("userArn") val userArn: String? = null,
            @SerialName("cognitoAuthenticationType") val cognitoAuthenticationType: String? = null,
            @SerialName("caller") val caller: String? = null,
            @SerialName("userAgent") val userAgent: String,
            @SerialName("user") val user: String? = null,
            @SerialName("cognitoAuthenticationProvider") val cognitoAuthenticationProvider: String? = null,
            @SerialName("sourceIp") val sourceIp: String,
            @SerialName("accountId") val accountId: String? = null,
            @SerialName("cognitoIdentityId") val cognitoIdentityId: String? = null,
        )

        @Serializable
        data class Authorizer(
            @SerialName("principalId") val principalId: String,
            @SerialName("clientId") val clientId: Int,
            @SerialName("clientName") val clientName: String,
        )
    }
}

@Serializable
data class APIGatewayResponse<T>(
    @SerialName("statusCode") val statusCode: Int,
    @SerialName("headers") val headers: Map<String, String>? = null,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, String>? = null,
    @SerialName("body") val body: T? = null,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean? = null
)
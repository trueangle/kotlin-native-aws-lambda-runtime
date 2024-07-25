package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIGatewayRequest<T>(
    @SerialName("resource") val resource: String,
    @SerialName("path") val path: String,
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>? = null,
    @SerialName("multiValueQueryStringParameters") val multiValueQueryStringParameters: Map<String, List<String>>? = null,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, String>,
    @SerialName("pathParameters") val pathParameters: Map<String, String>? = null,
    @SerialName("stageVariables") val stageVariables: Map<String, String>? = null,
    @SerialName("requestContext") val requestContext: Context,
    @SerialName("body") val body: T? = null,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
) {
    @Serializable
    data class Context(
        @SerialName("resourceId") val resourceId: String,
        @SerialName("apiId") val apiId: String,
        @SerialName("domainName") val domainName: String? = null,
        @SerialName("resourcePath") val resourcePath: String,
        @SerialName("httpMethod") val httpMethod: String,
        @SerialName("requestId") val requestId: String,
        @SerialName("accountId") val accountId: String,
        @SerialName("stage") val stage: String,
        @SerialName("identity") val identity: Identity,
        @SerialName("authorizer") val authorizer: Authorizer? = null,
        @SerialName("extendedRequestId") val extendedRequestId: String? = null,
        @SerialName("path") val path: String
    ) {
        @Serializable
        data class Identity(
            @SerialName("cognitoIdentityPoolId") val cognitoIdentityPoolId: String? = null,
            @SerialName("apiKey") val apiKey: String? = null,
            @SerialName("userArn") val userArn: String? = null,
            @SerialName("cognitoAuthenticationType") val cognitoAuthenticationType: String? = null,
            @SerialName("caller") val caller: String? = null,
            @SerialName("userAgent") val userAgent: String? = null,
            @SerialName("user") val user: String? = null,
            @SerialName("cognitoAuthenticationProvider") val cognitoAuthenticationProvider: String? = null,
            @SerialName("sourceIp") val sourceIp: String? = null,
            @SerialName("accountId") val accountId: String? = null
        )

        @Serializable
        data class Authorizer(
            @SerialName("claims") val claims: Map<String, String>? = null
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

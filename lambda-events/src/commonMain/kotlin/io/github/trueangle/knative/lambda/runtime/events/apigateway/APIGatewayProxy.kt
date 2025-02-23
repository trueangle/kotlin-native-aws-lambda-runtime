package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIGatewayProxy(
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
        @SerialName("identity") val identity: Identity?,
        @SerialName("resourcePath") val resourcePath: String?,
        @SerialName("httpMethod") val httpMethod: String,
        @SerialName("apiId") val apiId: String?,
        @SerialName("path") val path: String?,
        @SerialName("authorizer") val authorizer: Authorizer?,
        @SerialName("extendedRequestId") val extendedRequestId: String?,
        @SerialName("requestTime") val requestTime: String?,
        @SerialName("requestTimeEpoch") val requestTimeEpoch: Long,
        @SerialName("domainName") val domainName: String?,
        @SerialName("domainPrefix") val domainPrefix: String?,
        @SerialName("protocol") val protocol: String?
    )

    @Serializable
    data class Authorizer(
        @SerialName("claims") val claims: Map<String, String?>?,
        @SerialName("scopes") val scopes: String?
    )

    @Serializable
    data class Identity(
        @SerialName("cognitoIdentityPoolId") val cognitoIdentityPoolId: String?,
        @SerialName("accountId") val accountId: String?,
        @SerialName("cognitoIdentityId") val cognitoIdentityId: String?,
        @SerialName("caller") val caller: String?,
        @SerialName("principalOrgId") val principalOrgId: String?,
        @SerialName("sourceIp") val sourceIp: String?,
        @SerialName("cognitoAuthenticationType") val cognitoAuthenticationType: String?,
        @SerialName("cognitoAuthenticationProvider") val cognitoAuthenticationProvider: String?,
        @SerialName("userArn") val userArn: String?,
        @SerialName("userAgent") val userAgent: String?,
        @SerialName("user") val user: String?,
        @SerialName("accessKey") val accessKey: String?,
        @SerialName("clientCert") val clientCert: ClientCert?
    )

    @Serializable
    data class ClientCert(
        @SerialName("clientCertPem") val clientCertPem: String?,
        @SerialName("subjectDN") val subjectDN: String?,
        @SerialName("issuerDN") val issuerDN: String?,
        @SerialName("serialNumber") val serialNumber: String?,
        @SerialName("validity") val validity: Validity?
    )

    @Serializable
    data class Validity(
        @SerialName("notBefore") val notBefore: String?,
        @SerialName("notAfter") val notAfter: String?
    )
}
package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIGatewayV2Request(
    @SerialName("version") val version: String,
    @SerialName("routeKey") val routeKey: String,
    @SerialName("rawPath") val rawPath: String,
    @SerialName("rawQueryString") val rawQueryString: String,
    @SerialName("cookies") val cookies: List<String>?,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>?,
    @SerialName("pathParameters") val pathParameters: Map<String, String>?,
    @SerialName("requestContext") val context: Context,
    @SerialName("stageVariables") val stageVariables: Map<String, String>?,
    @SerialName("body") val body: String?,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
) {
    @Serializable
    data class Context(
        @SerialName("accountId") val accountId: String,
        @SerialName("apiId") val apiId: String,
        @SerialName("domainName") val domainName: String,
        @SerialName("domainPrefix") val domainPrefix: String,
        @SerialName("stage") val stage: String,
        @SerialName("requestId") val requestId: String,
        @SerialName("http") val http: HTTP,
        @SerialName("authorizer") val authorizer: Authorizer?,
        @SerialName("authentication") val authentication: Authentication?,
        @SerialName("time") val time: String,
        @SerialName("timeEpoch") val timeEpoch: Long
    ) {
        @Serializable
        data class HTTP(
            @SerialName("method") val method: String,
            @SerialName("path") val path: String,
            @SerialName("protocol") val protocol: String,
            @SerialName("sourceIp") val sourceIp: String,
            @SerialName("userAgent") val userAgent: String
        )

        @Serializable
        data class Authorizer(
            @SerialName("jwt") val jwt: JWT?,
            @SerialName("iam") val iam: IAM?,
            @SerialName("lambda") val lambda: Map<String, String>?
        ) {
            @Serializable
            data class JWT(
                @SerialName("claims") val claims: Map<String, String>?,
                @SerialName("scopes") val scopes: List<String>?
            )

            @Serializable
            data class IAM(
                @SerialName("accessKey") val accessKey: String?,
                @SerialName("accountId") val accountId: String?,
                @SerialName("callerId") val callerId: String?,
                @SerialName("cognitoIdentity") val cognitoIdentity: CognitoIdentity?,
                @SerialName("principalOrgId") val principalOrgId: String?,
                @SerialName("userArn") val userArn: String?,
                @SerialName("userId") val userId: String?
            ) {
                @Serializable
                data class CognitoIdentity(
                    @SerialName("amr") val amr: List<String>?,
                    @SerialName("identityId") val identityId: String?,
                    @SerialName("identityPoolId") val identityPoolId: String?
                )
            }
        }

        @Serializable
        data class Authentication(
            @SerialName("clientCert") val clientCert: ClientCert?
        ) {
            @Serializable
            data class ClientCert(
                @SerialName("clientCertPem") val clientCertPem: String,
                @SerialName("subjectDN") val subjectDN: String,
                @SerialName("issuerDN") val issuerDN: String,
                @SerialName("serialNumber") val serialNumber: String,
                @SerialName("validity") val validity: Validity
            ) {
                @Serializable
                data class Validity(
                    @SerialName("notBefore") val notBefore: String,
                    @SerialName("notAfter") val notAfter: String
                )
            }
        }
    }
}

@Serializable
data class APIGatewayV2Response(
    @SerialName("statusCode") val statusCode: Int,
    @SerialName("headers") val headers: Map<String, String>?,
    @SerialName("body") val body: String?,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean?,
    @SerialName("cookies") val cookies: List<String>?
)

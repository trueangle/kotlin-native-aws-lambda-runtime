package io.github.trueangle.knative.lambda.runtime.events.apigateway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class APIGatewayLambdaAuthorizerRequest(
    @SerialName("version") val version: String,
    @SerialName("type") val type: String,
    @SerialName("routeArn") val routeArn: String?,
    @SerialName("identitySource") val identitySource: List<String>,
    @SerialName("routeKey") val routeKey: String,
    @SerialName("rawPath") val rawPath: String,
    @SerialName("rawQueryString") val rawQueryString: String,
    @SerialName("headers") val headers: Map<String, String>,
    @SerialName("requestContext") val requestContext: Context?
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
    }
}

@Serializable
data class APIGatewayLambdaAuthorizerSimpleResponse(
    @SerialName("isAuthorized") val isAuthorized: Boolean,
    @SerialName("context") val context: Map<String, String>?
)

@Serializable
data class APIGatewayLambdaAuthorizerPolicyResponse(
    @SerialName("principalId") val principalId: String,
    @SerialName("policyDocument") val policyDocument: PolicyDocument,
    @SerialName("context") val context: Map<String, String>?
) {
    @Serializable
    data class PolicyDocument(
        @SerialName("version") val version: String,
        @SerialName("statement") val statement: List<Statement>
    ) {
        @Serializable
        data class Statement(
            @SerialName("action") val action: List<String>,
            @SerialName("effect") val effect: Effect,
            @SerialName("resource") val resource: List<String>
        ) {
            @Serializable
            enum class Effect(val value: String) {
                @SerialName("Allow")
                ALLOW("Allow"),
                @SerialName("Deny")
                DENY("Deny")
            }
        }
    }
}

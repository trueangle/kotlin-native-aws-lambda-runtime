package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ALBTargetGroupRequest(
    @SerialName("httpMethod") val httpMethod: String,
    @SerialName("path") val path: String,
    @SerialName("queryStringParameters") val queryStringParameters: Map<String, String>,
    @SerialName("headers") val headers: Map<String, String>?,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, String>?,
    @SerialName("requestContext") val requestContext: Context,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean,
    @SerialName("body") val body: String?
) {
    @Serializable
    data class Context(
        @SerialName("elb") val elb: ELBContext
    ) {
        @Serializable
        data class ELBContext(
            @SerialName("targetGroupArn") val targetGroupArn: String
        )
    }
}

@Serializable
data class ALBTargetGroupResponse(
    @SerialName("statusCode") val statusCode: Int,
    @SerialName("statusDescription") val statusDescription: String?,
    @SerialName("headers") val headers: Map<String, String>?,
    @SerialName("multiValueHeaders") val multiValueHeaders: Map<String, String>?,
    @SerialName("body") val body: String,
    @SerialName("isBase64Encoded") val isBase64Encoded: Boolean
)

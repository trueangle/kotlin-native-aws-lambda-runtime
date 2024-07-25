package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.*

@Serializable
data class BedrockAgentRequest(
    @SerialName("messageVersion") val messageVersion: String,
    @SerialName("agent") val agent: Agent?,
    @SerialName("sessionId") val sessionId: String?,
    @SerialName("sessionAttributes") val sessionAttributes: Map<String, String>?,
    @SerialName("promptSessionAttributes") val promptSessionAttributes: Map<String, String>?,
    @SerialName("inputText") val inputText: String?,
    @SerialName("apiPath") val apiPath: String?,
    @SerialName("actionGroup") val actionGroup: String?,
    @SerialName("httpMethod") val httpMethod: HTTPRequest.Method?,
    @SerialName("parameters") val parameters: List<Parameter>?,
    @SerialName("requestBody") val requestBody: RequestBody?
) {
    @Serializable
    data class Agent(
        @SerialName("alias") val alias: String,
        @SerialName("name") val name: String,
        @SerialName("version") val version: String,
        @SerialName("id") val id: String
    )

    @Serializable
    data class Parameter(
        @SerialName("name") val name: String,
        @SerialName("type") val type: String,
        @SerialName("value") val value: String
    )

    @Serializable
    data class RequestBody(
        @SerialName("content") val content: Map<String, Content>
    ) {
        @Serializable
        data class Content(
            @SerialName("properties") val properties: List<Parameter>
        )
    }
}

@Serializable
sealed class HTTPRequest {
    @Serializable
    enum class Method {
        @SerialName("GET")
        GET,
        @SerialName("POST")
        POST,
        @SerialName("PUT")
        PUT,
        @SerialName("DELETE")
        DELETE,
        @SerialName("PATCH")
        PATCH,
        @SerialName("OPTIONS")
        OPTIONS,
        @SerialName("HEAD")
        HEAD
    }
}

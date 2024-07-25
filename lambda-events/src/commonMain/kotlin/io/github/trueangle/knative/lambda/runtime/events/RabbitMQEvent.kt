package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RabbitMQEvent(
    @SerialName("eventSource") val eventSource: String,
    @SerialName("eventSourceArn") val eventSourceArn: String,
    @SerialName("rmqMessagesByQueue") val messagesByQueue: Map<String, List<Message>>
) {
    @Serializable
    data class Message(
        @SerialName("basicProperties") val basicProperties: BasicProperties,
        @SerialName("data") val data: String,
        @SerialName("redelivered") val redelivered: Boolean
    )

    @Serializable
    data class BasicProperties(
        @SerialName("contentType") val contentType: String,
        @SerialName("contentEncoding") val contentEncoding: String?,
        @SerialName("headers") val headers: Map<String, String?>,
        @SerialName("deliveryMode") val deliveryMode: UByte,
        @SerialName("priority") val priority: UByte,
        @SerialName("correlationId") val correlationId: String?,
        @SerialName("replyTo") val replyTo: String?,
        @SerialName("expiration") val expiration: String,
        @SerialName("messageId") val messageId: String?,
        @SerialName("timestamp") val timestamp: String,
        @SerialName("type") val type: String?,
        @SerialName("userId") val userId: String,
        @SerialName("appId") val appId: String?,
        @SerialName("clusterId") val clusterId: String?,
        @SerialName("bodySize") val bodySize: ULong
    )
}
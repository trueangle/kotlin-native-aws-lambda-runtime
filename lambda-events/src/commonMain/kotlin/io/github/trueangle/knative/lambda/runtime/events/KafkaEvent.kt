package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KafkaEvent(
    @SerialName("eventSource") val eventSource: String,
    @SerialName("eventSourceArn") val eventSourceArn: String,
    @SerialName("records") val records: Map<String, List<Record>>,
    @SerialName("bootstrapServers") val bootstrapServers: String
) {
    @Serializable
    data class Record(
        @SerialName("topic") val topic: String,
        @SerialName("partition") val partition: Long,
        @SerialName("offset") val offset: Long,
        @SerialName("timestamp") val timestamp: String,
        @SerialName("timestampType") val timestampType: String,
        @SerialName("key") val key: String? = null,
        @SerialName("value") val value: String? = null,
        @SerialName("headers") val headers: List<Map<String, ByteArray>>
    )
}


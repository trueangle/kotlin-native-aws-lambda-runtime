package io.github.trueangle.knative.lambda.runtime.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimeWindowProperties(
    @SerialName("window") val window: Window,
    @SerialName("state") val state: Map<String, String>,
    @SerialName("shardId") val shardId: String,
    @SerialName("eventSourceARN") val eventSourceArn: String,
    @SerialName("isFinalInvokeForWindow") val isFinalInvokeForWindow: Boolean,
    @SerialName("isWindowTerminatedEarly") val isWindowTerminatedEarly: Boolean
) {
    @Serializable
    data class Window(
        @SerialName("start") val start: String,
        @SerialName("end") val end: String
    )
}

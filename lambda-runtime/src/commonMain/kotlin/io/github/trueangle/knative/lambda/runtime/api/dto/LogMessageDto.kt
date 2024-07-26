package io.github.trueangle.knative.lambda.runtime.api.dto

import io.github.trueangle.knative.lambda.runtime.log.LogLevel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LogMessageDto(
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("message")
    val message: String,
    @SerialName("level")
    val level: LogLevel,
    @SerialName("AWSRequestId")
    val awsRequestId: String
)
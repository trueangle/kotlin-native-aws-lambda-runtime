package io.github.trueangle.knative.lambda.runtime.api.dto

import io.github.trueangle.knative.lambda.runtime.log.LogLevel
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal data class LogMessageDto<T>(
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("message")
    val message: T?,
    @SerialName("level")
    val level: LogLevel,
    @SerialName("AWSRequestId")
    val awsRequestId: String? = null
)
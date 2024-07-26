package io.github.trueangle.knative.lambda.runtime.api.dto

import io.github.trueangle.knative.lambda.runtime.LambdaRuntimeException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InvocationErrorDto(
    @SerialName("errorMessage")
    val message: String,
    @SerialName("errorType")
    val type: String,
    @SerialName("stackTrace")
    val stackTrace: String
)

internal fun LambdaRuntimeException.toDto() = InvocationErrorDto(message = requireNotNull(message), type = type, stackTrace = stackTraceToString())
package io.github.trueangle.knative.lambda.runtime.api.dto

import io.github.trueangle.knative.lambda.runtime.api.LambdaRuntimeError
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvocationErrorDto(
    @SerialName("errorMessage")
    val message: String,
    @SerialName("errorType")
    val type: String,
    @SerialName("stackTrace")
    val stackTrace: String
)

fun LambdaRuntimeError.toDto() = InvocationErrorDto(message = message, type = type, stackTrace = stackTrace)
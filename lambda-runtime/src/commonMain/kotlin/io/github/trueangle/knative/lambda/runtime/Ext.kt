package io.github.trueangle.knative.lambda.runtime

import kotlinx.serialization.Serializable
import kotlin.experimental.ExperimentalNativeApi

internal fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

internal fun Throwable.prettyPrint(includeStackTrace: Boolean = true) = buildString {
    append("An exception occurred:\n")
    message?.let {
        append("Message: ${message}\n")
    }
    if (includeStackTrace) {
        append("Stack Trace:\n")
        append(stackTraceToString())
    }
}

@OptIn(ExperimentalNativeApi::class)
internal fun Throwable.asSerialObject(): ThrowableDto = ThrowableDto(
    message = message,
    cause = cause?.asSerialObject(),
    stackTrace = getStackTrace().toList()
)

@Serializable
internal data class ThrowableDto(
    val message: String?,
    val cause: ThrowableDto?,
    val stackTrace: List<String>,
)
package io.github.trueangle.knative.lambda.runtime

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

internal fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)
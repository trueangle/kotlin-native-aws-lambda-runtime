package io.github.trueangle.knative.lambda.runtime.log

internal fun Throwable.prettyPrint() = buildString {
    append("An exception occurred:\n")
    append("Exception Type: ${this::class.simpleName}\n")
    append("Message: ${message}\n")
    append("Stack Trace:\n")
    append(stackTraceToString())
}
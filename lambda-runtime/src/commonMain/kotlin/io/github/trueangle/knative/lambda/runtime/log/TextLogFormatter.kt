package io.github.trueangle.knative.lambda.runtime.log

import io.ktor.util.reflect.TypeInfo

internal class TextLogFormatter : LogFormatter {
    override fun <T> format(logLevel: LogLevel, message: T?, typeInfo: TypeInfo) = message?.let {
        buildString {
            append("[${logLevel.toString().uppercase()}] | ")
            append(if (message is Throwable) message.prettyPrint() else message.toString())
        }
    }
}
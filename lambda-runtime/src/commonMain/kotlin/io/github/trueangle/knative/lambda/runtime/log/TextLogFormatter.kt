package io.github.trueangle.knative.lambda.runtime.log

internal class TextLogFormatter : LogFormatter {
    override fun format(logLevel: LogLevel, message: Any?): String? = message?.let {
        buildString {
            append("[${logLevel.toString().uppercase()}] | ")
            append(if (message is Throwable) message.prettyPrint() else message.toString())
        }
    }
}
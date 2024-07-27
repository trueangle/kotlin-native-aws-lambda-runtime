package io.github.trueangle.knative.lambda.runtime.log

import io.github.trueangle.knative.lambda.runtime.api.Context

interface LambdaLogger {
    fun trace(message: Any?)
    fun debug(message: Any?)
    fun info(message: Any?)
    fun warn(message: Any?)
    fun error(message: Any?)
    fun fatal(message: Any?)
}

interface LogWriter {
    fun write(level: LogLevel, message: Any?)
}

interface LogFormatter {
    fun format(logLevel: LogLevel, message: Any?): Any?
    fun onContextAvailable(context: Context) {}
}
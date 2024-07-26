package io.github.trueangle.knative.lambda.runtime.log

/**
 * Each method corresponds to LogLevel
 */
interface LambdaLogger {
    fun t(message: Any?)
    fun d(message: Any?)
    fun i(message: Any?)
    fun w(message: Any?)
    fun e(message: Any?)
    fun f(message: Any?)
}

interface LogWriter {
    fun write(level: LogLevel, message: Any?)
}

interface LogFormatter {
    fun format(logLevel: LogLevel, message: Any?): Any?
}
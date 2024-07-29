package io.github.trueangle.knative.lambda.runtime.log

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironment
import io.github.trueangle.knative.lambda.runtime.api.Context

internal interface LogWriter {
    fun write(level: LogLevel, message: Any?)
}

internal interface LogFormatter {
    fun format(logLevel: LogLevel, message: Any?): Any?
    fun onContextAvailable(context: Context) {}
}

object Log {
    @PublishedApi
    internal val currentLogLevel = LogLevel.fromEnv()
    private val writer = StdoutLogWriter()
    private val logFormatter = if (LambdaEnvironment.LAMBDA_LOG_FORMAT == "JSON") {
        JsonLogFormatter()
    } else {
        TextLogFormatter()
    }

    fun trace(message: Any?) {
        write(LogLevel.TRACE, message)
    }

    fun debug(message: Any?) {
        write(LogLevel.DEBUG, message)
    }

    fun info(message: Any?) {
        write(LogLevel.INFO, message)
    }

    fun warn(message: Any?) {
        write(LogLevel.WARN, message)
    }

    fun error(message: Any?) {
        write(LogLevel.ERROR, message)
    }

    fun fatal(message: Any?) {
        write(LogLevel.FATAL, message)
    }

    @PublishedApi
    internal fun setContext(context: Context) {
        logFormatter.onContextAvailable(context)
    }

    private fun write(level: LogLevel, message: Any?) {
        if (level >= currentLogLevel) {
            writer.write(level, logFormatter.format(level, message))
        }
    }
}
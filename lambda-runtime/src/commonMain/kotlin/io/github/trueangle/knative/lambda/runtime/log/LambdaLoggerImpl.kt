package io.github.trueangle.knative.lambda.runtime.log

import io.github.trueangle.knative.lambda.runtime.api.Context

@PublishedApi
internal class LambdaLoggerImpl(
    private val writer: LogWriter,
    private val logFormatter: LogFormatter
) : LambdaLogger {
    private val currentLogLevel = LogLevel.fromEnv()

    override fun trace(message: Any?) {
        write(LogLevel.TRACE, message)
    }

    override fun debug(message: Any?) {
        write(LogLevel.DEBUG, message)
    }

    override fun info(message: Any?) {
        write(LogLevel.INFO, message)
    }

    override fun warn(message: Any?) {
        write(LogLevel.WARN, message)
    }

    override fun error(message: Any?) {
        if (message is Throwable) {
            write(LogLevel.FATAL, message) // todo
        } else {
            write(LogLevel.ERROR, message)
        }
    }

    override fun fatal(message: Any?) {
        if (message is Throwable) {
            write(LogLevel.FATAL, message) // todo
        } else {
            write(LogLevel.FATAL, message)
        }
    }

    fun setContext(context: Context) {
        logFormatter.onContextAvailable(context)
    }

    private fun write(level: LogLevel, message: Any?) {
        if (level >= currentLogLevel) {
            writer.write(level, logFormatter.format(level, message))
        }
    }
}
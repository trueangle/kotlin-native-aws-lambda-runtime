package io.github.trueangle.knative.lambda.runtime.log

@PublishedApi
internal class LambdaLoggerImpl(
    private val writer: LogWriter,
    private val logFormatter: LogFormatter
) : LambdaLogger {

    private val currentLogLevel = LogLevel.fromEnv()

    override fun t(message: Any?) {
        write(LogLevel.TRACE, message)
    }

    override fun d(message: Any?) {
        write(LogLevel.DEBUG, message)
    }

    override fun i(message: Any?) {
        write(LogLevel.INFO, message)
    }

    override fun w(message: Any?) {
        write(LogLevel.WARN, message)
    }

    override fun e(message: Any?) {
        write(LogLevel.ERROR, message)
    }

    override fun f(message: Any?) {
        write(LogLevel.FATAL, message)
    }

    private fun write(level: LogLevel, message: Any?) {
        if (level >= currentLogLevel) {
            writer.write(level, logFormatter.format(level, message))
        }
    }
}
package io.github.trueangle.knative.lambda.runtime.log

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironment
import io.github.trueangle.knative.lambda.runtime.LambdaRuntime
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo

internal interface LogWriter {
    fun write(level: LogLevel, message: Any?)
}

internal interface LogFormatter {
    fun <T> format(logLevel: LogLevel, message: T?, messageType: TypeInfo): String?
    fun onContextAvailable(context: Context) = Unit
}

object Log {
    @PublishedApi
    internal val currentLogLevel = LogLevel.fromEnv()
    private val writer = StdoutLogWriter()
    private val logFormatter = if (LambdaEnvironment.LAMBDA_LOG_FORMAT.equals("JSON", ignoreCase = true)) {
        JsonLogFormatter(LambdaRuntime.json)
    } else {
        TextLogFormatter()
    }

    inline fun <reified T> trace(message: T?) {
        write(LogLevel.TRACE, message, typeInfo<T>())
    }

    inline fun <reified T> debug(message: T?) {
        write(LogLevel.DEBUG, message, typeInfo<T>())
    }

    inline fun <reified T> info(message: T?) {
        write(LogLevel.INFO, message, typeInfo<T>())
    }

    inline fun <reified T> warn(message: T?) {
        write(LogLevel.WARN, message, typeInfo<T>())
    }

    inline fun <reified T> error(message: T?) {
        write(LogLevel.ERROR, message, typeInfo<T>())
    }

    inline fun <reified T> fatal(message: T?) {
        write(LogLevel.FATAL, message, typeInfo<T>())
    }

    @PublishedApi
    internal fun setContext(context: Context) {
        logFormatter.onContextAvailable(context)
    }

    @PublishedApi
    internal fun write(level: LogLevel, message: Any?, typeInfo: TypeInfo) {
        if (level >= currentLogLevel) {
            writer.write(level, logFormatter.format(level, message, typeInfo))
        }
    }
}
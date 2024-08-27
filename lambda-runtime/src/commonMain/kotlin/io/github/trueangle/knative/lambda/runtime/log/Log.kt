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

interface LambdaLogger {
    fun <T> log(level: LogLevel, message: T?, typeInfo: TypeInfo)

    fun setContext(context: Context)
}

object Log : LambdaLogger {
    internal val currentLogLevel = LogLevel.fromEnv()
    private val writer = StdoutLogWriter()
    private val logFormatter = if (LambdaEnvironment.LAMBDA_LOG_FORMAT.equals("JSON", ignoreCase = true)) {
        JsonLogFormatter(LambdaRuntime.json)
    } else {
        TextLogFormatter()
    }

    override fun setContext(context: Context) {
        logFormatter.onContextAvailable(context)
    }

    override fun <T> log(level: LogLevel, message: T?, typeInfo: TypeInfo) {
        if (level >= currentLogLevel) {
            writer.write(level, logFormatter.format(level, message, typeInfo))
        }
    }
}

inline fun <reified T> LambdaLogger.trace(message: T?) {
    log(LogLevel.TRACE, message, typeInfo<T>())
}

inline fun <reified T : Any> LambdaLogger.debug(message: T?) {
    log(LogLevel.DEBUG, message, typeInfo<T>())
}

inline fun <reified T : Any> LambdaLogger.info(message: T?) {
    log(LogLevel.INFO, message, typeInfo<T>())
}

inline fun <reified T : Any> LambdaLogger.warn(message: T?) {
    log(LogLevel.WARN, message, typeInfo<T>())
}

inline fun <reified T : Any> LambdaLogger.error(message: T?) {
    log(LogLevel.ERROR, message, typeInfo<T>())
}

inline fun <reified T : Any> LambdaLogger.fatal(message: T?) {
    log(LogLevel.FATAL, message, typeInfo<T>())
}
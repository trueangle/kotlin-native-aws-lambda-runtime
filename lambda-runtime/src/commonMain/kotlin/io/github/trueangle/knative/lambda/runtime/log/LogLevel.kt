package io.github.trueangle.knative.lambda.runtime.log

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironment

/**
 *  Represents log levels configured for each lambda in "Configuration->Monitoring and operations tools".
 *  Log events emitted with a lower log level than the one selected are not published to the function’s CloudWatch log stream.
 *  For example, setting the function’s log level to INFO results in DEBUG log events being ignored.
 */
enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL;

    companion object {
        fun fromEnv(): LogLevel {
            val level = LambdaEnvironment.LAMBDA_LOG_LEVEL ?: "INFO"
            return runCatching {
                valueOf(level)
            }.getOrElse {
                throw IllegalArgumentException(
                    "Invalid log level: '$level' expected one of [TRACE, DEBUG, INFO, WARN, ERROR, FATAL]"
                )
            }
        }
    }
}
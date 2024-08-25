package io.github.trueangle.knative.lambda.runtime.log

import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.LogLevel as KtorLogLevel

internal class KtorLogger : Logger {
    override fun log(message: String) = when (getLevel()) {
        KtorLogLevel.ALL -> Log.trace(message)
        KtorLogLevel.BODY -> Log.debug(message)
        else -> Unit
    }

    fun getLevel() = when (Log.currentLogLevel) {
        LogLevel.TRACE -> KtorLogLevel.ALL
        LogLevel.DEBUG -> KtorLogLevel.BODY
        else -> KtorLogLevel.NONE
    }
}


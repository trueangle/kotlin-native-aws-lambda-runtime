package io.github.trueangle.knative.lambda.runtime.api

sealed interface LambdaRuntimeError {
    val message: String
    val type: String
    val stackTrace: String

    sealed interface Init : LambdaRuntimeError {
        data class Failed(
            override val message: String,
            override val type: String = "Runtime.InitFailed",
            override val stackTrace: String,
        ) : Init
    }

    sealed interface Invocation : LambdaRuntimeError {
        val context: Context

        data class HandlerError(
            override val context: Context,
            override val message: String,
            override val type: String = "Runtime.HandlerError",
            override val stackTrace: String
        ) : Invocation

        data class Unknown(
            override val context: Context,
            override val message: String,
            override val type: String = "Runtime.UnknownReason",
            override val stackTrace: String
        ) : Invocation
    }
}

fun Throwable.asHandlerError(context: Context) = LambdaRuntimeError.Invocation.HandlerError(
    context = context,
    message = message.orEmpty(),
    stackTrace = stackTraceToString()
)

fun Throwable.asUnknownInvocationError(context: Context) = LambdaRuntimeError.Invocation.Unknown(
    context = context,
    message = message.orEmpty(),
    stackTrace = stackTraceToString()
)

fun Throwable.asInitError() = LambdaRuntimeError.Init.Failed(
    message = message.orEmpty(),
    stackTrace = stackTraceToString()
)
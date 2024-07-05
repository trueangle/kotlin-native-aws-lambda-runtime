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

        data class NoSuchHandler(
            override val message: String,
            override val type: String = "Runtime.NoSuchHandler",
            override val stackTrace: String
        ) : Init
    }

    sealed interface Invocation : LambdaRuntimeError {
        val context: Context

        data class Unknown(
            override val context: Context,
            override val message: String,
            override val type: String = "Runtime.UnknownReason",
            override val stackTrace: String
        ) : Invocation
    }
}

fun Throwable.toUnknownInvocationError(context: Context) = LambdaRuntimeError.Invocation.Unknown(
    context = context,
    message = message.orEmpty(),
    stackTrace = stackTraceToString()
)

fun Throwable.toInitInvocationError() = LambdaRuntimeError.Init.Failed(
    message = message.orEmpty(),
    stackTrace = stackTraceToString()
)
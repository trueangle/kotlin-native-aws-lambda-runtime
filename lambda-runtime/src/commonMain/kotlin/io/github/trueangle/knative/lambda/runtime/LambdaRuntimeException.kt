package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.LambdaRuntimeException.Invocation.HandlerException
import io.github.trueangle.knative.lambda.runtime.api.Context

sealed class LambdaRuntimeException : Throwable() {
    abstract val type: String

    sealed class Init : LambdaRuntimeException() {
        data class Failed(
            override val cause: Throwable,
            override val message: String = cause.message.orEmpty(),
            override val type: String = "Runtime.InitFailed",
        ) : Init()
    }

    sealed class Invocation : LambdaRuntimeException() {
        abstract val context: Context

        data class EventBodyParseException(
            override val cause: Throwable,
            override val context: Context,
            override val message: String = cause.message.orEmpty(),
            override val type: String = "Runtime.InvalidEventBodyError",
        ) : Invocation()

        data class HandlerException(
            override val cause: Throwable,
            override val context: Context,
            override val message: String = cause.message.orEmpty(),
            override val type: String = "Runtime.HandlerError",
        ) : Invocation()

        data class Unknown(
            override val cause: Throwable,
            override val context: Context,
            override val message: String = cause.message.orEmpty(),
            override val type: String = "Runtime.UnknownReason",
        ) : Invocation()
    }
}

fun Throwable.asHandlerError(context: Context) = HandlerException(
    context = context,
    message = message.orEmpty(),
    cause = this
)

fun Throwable.asUnknownInvocationError(context: Context) = LambdaRuntimeException.Invocation.Unknown(
    context = context,
    cause = this
)

fun Throwable.asInitError() = LambdaRuntimeException.Init.Failed(
    message = message.orEmpty(),
    cause = this
)
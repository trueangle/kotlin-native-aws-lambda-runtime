package io.github.trueangle.knative.lambda.runtime

@PublishedApi
internal sealed class LambdaEnvironmentException : IllegalStateException() {
    class NonRecoverableStateException(override val message: String = "Container error. Non-recoverable state") : LambdaEnvironmentException()
    class ForbiddenException(override val message: String) : LambdaEnvironmentException()
    class BadRequestException(override val message: String) : LambdaEnvironmentException()
    class CommonException(override val message: String) : LambdaEnvironmentException()
}
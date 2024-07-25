package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_MEMORY_SIZE
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_NAME
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_VERSION
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_LOG_FORMAT
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_LOG_GROUP_NAME
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_LOG_LEVEL
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_LOG_STREAM_NAME
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_RUNTIME_API
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
object LambdaEnvironment {
    val MEMORY_LIMIT = getenv(AWS_LAMBDA_FUNCTION_MEMORY_SIZE)?.toKString()?.toIntOrNull() ?: 128
    val LOG_GROUP_NAME: String = getenv(AWS_LAMBDA_LOG_GROUP_NAME)?.toKString().orEmpty()
    val LOG_STREAM_NAME: String = getenv(AWS_LAMBDA_LOG_STREAM_NAME)?.toKString().orEmpty()
    val LAMBDA_LOG_LEVEL: String? = getenv(AWS_LAMBDA_LOG_LEVEL)?.toKString()
    val LAMBDA_LOG_FORMAT: String? = getenv(AWS_LAMBDA_LOG_FORMAT)?.toKString()
    val FUNCTION_NAME: String = getenv(AWS_LAMBDA_FUNCTION_NAME)?.toKString().orEmpty()
    val FUNCTION_VERSION: String = getenv(AWS_LAMBDA_FUNCTION_VERSION)?.toKString().orEmpty()

    internal val RUNTIME_API: String = requireNotNull(getenv(AWS_LAMBDA_RUNTIME_API)?.toKString()) {
        "Can't find AWS_LAMBDA_RUNTIME_API env variable"
    }
}

internal object ReservedRuntimeEnvironmentVariables {
    /**
     * The handler location configured on the function.
     */
    const val HANDLER: String = "_HANDLER"

    /**
     * The AWS Region where the Lambda function is executed.
     */
    const val AWS_REGION: String = "AWS_REGION"

    /**
     * The runtime identifier, prefixed by AWS_Lambda_—for example, AWS_Lambda_java8.
     */
    const val AWS_EXECUTION_ENV: String = "AWS_EXECUTION_ENV"

    /**
     * The name of the function.
     */
    const val AWS_LAMBDA_FUNCTION_NAME: String = "AWS_LAMBDA_FUNCTION_NAME"

    /**
     * The amount of memory available to the function in MB.
     */
    const val AWS_LAMBDA_FUNCTION_MEMORY_SIZE: String = "AWS_LAMBDA_FUNCTION_MEMORY_SIZE"

    /**
     * The version of the function being executed.
     */
    const val AWS_LAMBDA_FUNCTION_VERSION: String = "AWS_LAMBDA_FUNCTION_VERSION"

    /**
     * The name of the Amazon CloudWatch Logs group for the function.
     */
    const val AWS_LAMBDA_LOG_GROUP_NAME: String = "AWS_LAMBDA_LOG_GROUP_NAME"

    /**
     * The name of the Amazon CloudWatch stream for the function.
     */
    const val AWS_LAMBDA_LOG_STREAM_NAME: String = "AWS_LAMBDA_LOG_STREAM_NAME"

    /**
     * The logging level set for the function.
     */
    const val AWS_LAMBDA_LOG_LEVEL: String = "AWS_LAMBDA_LOG_LEVEL"

    /**
     * The logging format set for the function.
     */
    const val AWS_LAMBDA_LOG_FORMAT: String = "AWS_LAMBDA_LOG_FORMAT"

    /**
     * Access key id obtained from the function's execution role.
     */
    const val AWS_ACCESS_KEY_ID: String = "AWS_ACCESS_KEY_ID"

    /**
     * secret access key obtained from the function's execution role.
     */
    const val AWS_SECRET_ACCESS_KEY: String = "AWS_SECRET_ACCESS_KEY"

    /**
     * The access keys obtained from the function's execution role.
     */
    const val AWS_SESSION_TOKEN: String = "AWS_SESSION_TOKEN"

    /**
     * (Custom runtime) The host and port of the runtime API.
     */
    const val AWS_LAMBDA_RUNTIME_API: String = "AWS_LAMBDA_RUNTIME_API"

    /**
     * Initialization type
     */
    const val AWS_LAMBDA_INITIALIZATION_TYPE: String = "AWS_LAMBDA_INITIALIZATION_TYPE"

    /**
     * The path to your Lambda function code.
     */
    const val LAMBDA_TASK_ROOT: String = "LAMBDA_TASK_ROOT"

    /**
     * The path to runtime libraries.
     */
    const val LAMBDA_RUNTIME_DIR: String = "LAMBDA_RUNTIME_DIR"

    /**
     * The environment's time zone (UTC). The execution environment uses NTP to synchronize the system clock.
     */
    const val TZ: String = "TZ"
}
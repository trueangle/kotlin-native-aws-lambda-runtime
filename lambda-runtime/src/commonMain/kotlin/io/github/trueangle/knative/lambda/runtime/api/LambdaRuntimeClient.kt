package io.github.trueangle.knative.lambda.runtime.api

import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.BadRequestException
import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.CommonException
import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.ForbiddenException
import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.NonRecoverableStateException
import io.github.trueangle.knative.lambda.runtime.LambdaRuntimeException
import io.github.trueangle.knative.lambda.runtime.LambdaRuntimeException.Invocation.EventBodyParseException
import io.github.trueangle.knative.lambda.runtime.api.dto.toDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalForeignApi::class)
class LambdaClient(private val httpClient: HttpClient) {
    private val lambdaEnvApiEndpoint = requireNotNull(getenv("AWS_LAMBDA_RUNTIME_API")?.toKString()) {
        "Can't find AWS_LAMBDA_RUNTIME_API env variable"
    }
    private val invokeUrl = "http://${lambdaEnvApiEndpoint}/2018-06-01/runtime"

    suspend fun <T> retrieveNextEvent(bodyType: TypeInfo): InvocationEvent<T> {
        val response = httpClient.get {
            url("${invokeUrl}/invocation/next")
            timeout {
                requestTimeoutMillis = 60 * 1000 * 30 // todo
            }
        }

        val context = contextFromResponseHeaders(response)
        val body = try {
            response.body(bodyType) as T
        } catch (e: Exception) {
            throw EventBodyParseException(cause = e, context = context)
        }

        return InvocationEvent(body, context)
    }

    suspend fun <T> sendResponse(event: Context, body: T, bodyType: TypeInfo): HttpResponse {
        println("sendResponse from handler: $body")

        val response = httpClient.post {
            url("${invokeUrl}/invocation/${event.awsRequestId}/response")
            contentType(Json)
            headers {
                if (event.xrayTracingId != null) {
                    append("Lambda-Runtime-Trace-Id", event.xrayTracingId)
                    append("_X_AMZN_TRACE_ID", event.xrayTracingId)
                }
            }

            setBody(body, bodyType)
        }

        return validateResponse(response)
    }

    suspend fun streamResponse(event: Context, outgoingContent: OutgoingContent): HttpResponse {
        val response = httpClient.post {
            url("${invokeUrl}/invocation/${event.awsRequestId}/response")

            timeout {
                requestTimeoutMillis = 30.minutes.inWholeMilliseconds // todo
            }

            headers {
                if (event.xrayTracingId != null) {
                    append("Lambda-Runtime-Trace-Id", event.xrayTracingId)
                    append("_X_AMZN_TRACE_ID", event.xrayTracingId)
                }

                append("Lambda-Runtime-Function-Response-Mode", "streaming")
                append("Trailer", "Lambda-Runtime-Function-Error-Type")
                append("Trailer", "Lambda-Runtime-Function-Error-Body")
            }

            setBody(outgoingContent)
        }

        return response
    }

    suspend fun reportError(error: LambdaRuntimeException) {
        val response = when (error) {
            is LambdaRuntimeException.Init -> sendInitError(error)
            is LambdaRuntimeException.Invocation -> sendInvocationError(error)
        }

        validateResponse(response)
    }

    private suspend fun sendInvocationError(
        error: LambdaRuntimeException.Invocation
    ) = httpClient.post {
        val context = error.context

        url("${invokeUrl}/invocation/${context.awsRequestId}/error")
        contentType(Json)

        headers {
            append("Lambda-Runtime-Function-Error-Type", error.type)
            if (context.xrayTracingId != null) {
                append("Lambda-Runtime-Trace-Id", context.xrayTracingId)
                append("_X_AMZN_TRACE_ID", context.xrayTracingId)
            }
        }

        setBody(error.toDto())
    }

    private suspend fun sendInitError(
        error: LambdaRuntimeException.Init
    ) = httpClient.post {
        url("${invokeUrl}/init/error")
        setBody(error.toDto())
        headers {
            contentType(Json)
            append("Lambda-Runtime-Function-Error-Type", error.type)
        }
    }

    private suspend fun validateResponse(response: HttpResponse): HttpResponse {
        val body = response.body<String>()

        if (response.status != HttpStatusCode.Accepted) {
            when (response.status) {
                HttpStatusCode.InternalServerError -> throw NonRecoverableStateException("Non-recoverable state. Response from lambda environment: $body")
                HttpStatusCode.Forbidden -> throw ForbiddenException("Forbidden returned by the environment. Response from lambda environment: $body")
                HttpStatusCode.BadRequest -> throw BadRequestException("BadRequestException returned by the environment. Response from lambda environment: $body")
                else -> throw CommonException("Unexpected HTTP error returned by the environment. Status: ${response.status}, error body: $body")
            }
        }

        return response
    }

    private fun contextFromResponseHeaders(response: HttpResponse): Context {
        val requestId = requireNotNull(response.headers["Lambda-Runtime-Aws-Request-Id"])
        val deadlineTimeInMs =
            requireNotNull(response.headers["Lambda-Runtime-Deadline-Ms"]).toLong()
        val invokedFunctionArn =
            requireNotNull(response.headers["Lambda-Runtime-Invoked-Function-Arn"])
        val clientContext = response.headers["Lambda-Runtime-Client-Context"]
        val cognitoIdentity = response.headers["Lambda-Runtime-Cognito-Identity"]
        val xrayTraceId = response.headers["Lambda-Runtime-Trace-Id"]

        return Context(
            awsRequestId = requestId,
            deadlineTimeInMs = deadlineTimeInMs,
            invokedFunctionArn = invokedFunctionArn,
            clientContext = clientContext,
            cognitoIdentity = cognitoIdentity,
            xrayTracingId = xrayTraceId
        )
    }
}

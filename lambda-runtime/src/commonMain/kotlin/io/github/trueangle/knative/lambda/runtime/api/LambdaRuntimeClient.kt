package io.github.trueangle.knative.lambda.runtime.api

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
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ChannelWriterContent
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.writeFully
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
class LambdaClient(private val httpClient: HttpClient) {
    private val lambdaEnvApiEndpoint = requireNotNull(getenv("AWS_LAMBDA_RUNTIME_API")?.toKString()) {
        "Can't find AWS_LAMBDA_RUNTIME_API env variable"
    }
    private val invokeUrl = "http://${lambdaEnvApiEndpoint}/2018-06-01/runtime"

    suspend fun <T> retrieveNextEvent(bodyType: TypeInfo): InvocationEvent<T> {
        println("RetrieveNextEvent")
        val response = httpClient.get {
            url("${invokeUrl}/invocation/next")
            timeout {
                requestTimeoutMillis = 60 * 1000 * 30 // todo
            }
        }
        println("lambda api respone: $response")

        if (response.status != HttpStatusCode.OK) {
            throw LambdaClientException("Status 200 is expected for next invocation, got: $response")
        }

        return InvocationEvent(response.body(bodyType), contextFromResponse(response))
    }

    suspend fun <T> sendResponse(event: Context, body: T, bodyType: TypeInfo): HttpResponse {
        println("sendResponse from handler: $body")

        return httpClient.post {
            url("${invokeUrl}/invocation/${event.awsRequestId}/response")
            contentType(Json)
            headers {
                if (event.xrayTracingId != null) {
                    append("Lambda-Runtime-Trace-Id", event.xrayTracingId)
                    append("_X_AMZN_TRACE_ID", event.xrayTracingId)
                }
            }

            // todo Do we need to set body type directly?
            setBody(body, bodyType)
        }
    }

    // todo exceptions, buffer overflow
    // Flow or Channel?
    suspend fun streamResponse(event: Context, streamingBody: Flow<ByteArray>): HttpResponse = httpClient.post {
        url("${invokeUrl}/invocation/${event.awsRequestId}/response")
        headers {
            if (event.xrayTracingId != null) {
                append("Lambda-Runtime-Trace-Id", event.xrayTracingId)
                append("_X_AMZN_TRACE_ID", event.xrayTracingId)
            }

            append(HttpHeaders.TransferEncoding, "chunked")
            append("Lambda-Runtime-Function-Response-Mode", "streaming")
        }

        setBody(
            ChannelWriterContent(
                body = {
                    streamingBody
                        .onCompletion {
                            close(null)
                        }
                        .collect {
                            writeFully(it)
                        }
                },
                contentType = Json
            )
        )
    }

    suspend fun sendError(error: LambdaRuntimeError) {
        val response = when (error) {
            is LambdaRuntimeError.Init -> sendInitError(error)
            is LambdaRuntimeError.Invocation -> sendInvocationError(error)
        }

        if (response.status != HttpStatusCode.Accepted) {
            if (response.status == HttpStatusCode.InternalServerError) {
                throw NonRecoverableStateException()
            }

            throw LambdaClientException("Status 200 expected for next invocation, got: $response")
        }
    }

    private suspend fun sendInvocationError(
        error: LambdaRuntimeError.Invocation
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
        error: LambdaRuntimeError.Init
    ) = httpClient.post {
        url("${invokeUrl}/init/error")
        setBody(error.toDto())
        headers {
            contentType(Json)
            append("Lambda-Runtime-Function-Error-Type", error.type)
        }
    }

    private fun contextFromResponse(response: HttpResponse): Context {
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

class LambdaClientException(override val message: String) : IllegalStateException()
class NonRecoverableStateException(override val message: String = "Container error. Non-recoverable state. ") : IllegalStateException()
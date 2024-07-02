package io.github.trueangle.knative.lambda.runtime

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.content.TextContent
import io.ktor.http.ContentType.Application.Json
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

// add hooks and handle init error
// https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html
@OptIn(ExperimentalForeignApi::class)
class LambdaClient(private val httpClient: HttpClient) {
    private val lambdaEnvApiEndpoint = requireNotNull(getenv("AWS_LAMBDA_RUNTIME_API")?.toKString())
    private val invokeUrl = "http://${lambdaEnvApiEndpoint}/2018-06-01/runtime"

    suspend fun retrieveNextEvent(): InvocationEvent {
        println("RetrieveNextEvent")
        println("Request url ${invokeUrl}/invocation/next")
        val response = httpClient.get {
            url("${invokeUrl}/invocation/next")
            timeout {
                requestTimeoutMillis = 60 * 1000 * 30
            }
        }
        println("lambda api respone: $response")

        require(response.status.value == 200) { "Status 200 expected for next invocation, got: $response" }
        return InvocationEvent(response.body(), contextFromResponse(response))
    }

    suspend fun sendResponse(event: EventContext, body: String): HttpResponse {
        println("sendResponse from handler: $body")

        return httpClient.post {
            url("${invokeUrl}/invocation/${event.requestId}/response")
            headers {
                if (event.xrayTraceId != null) {
                    append("Lambda-Runtime-Trace-Id", event.xrayTraceId)
                    append("_X_AMZN_TRACE_ID", event.xrayTraceId)
                }
            }

            setBody(TextContent(body, Json))
        }
    }

    suspend fun sendLambdaInvocationError(
        event: EventContext,
        errorType: String,
        errorMessage: String
    ): HttpResponse {
        val body = """{"errorMessage":${errorMessage},"errorType":${errorType}}"""

        return httpClient.post {
            url("${invokeUrl}/invocation/${event.requestId}/error")
            setBody(TextContent(body, Json))
            headers {
                append("Lambda-Runtime-Function-Error-Type", errorType)
                if (event.xrayTraceId != null) {
                    append("Lambda-Runtime-Trace-Id", event.xrayTraceId)
                    append("_X_AMZN_TRACE_ID", event.xrayTraceId)
                }
            }
        }
    }

    suspend fun sendRuntimeError(
        event: EventContext,
        errorType: String,
        errorMessage: String
    ): HttpResponse {
        val body = """{"errorMessage":${errorMessage},"errorType":${errorType}}"""

        return httpClient.post {
            url("${invokeUrl}/init/error")
            setBody(TextContent(body, Json))
            headers {
                append("Lambda-Runtime-Function-Error-Type", errorType)

                if (event.xrayTraceId != null) {
                    append("_X_AMZN_TRACE_ID", event.xrayTraceId)
                }
            }
        }
    }

    private fun contextFromResponse(response: HttpResponse): EventContext {
        val requestId = requireNotNull(response.headers["Lambda-Runtime-Aws-Request-Id"])
        val deadlineMs = requireNotNull(response.headers["Lambda-Runtime-Deadline-Ms"]).toLong()
        val invokedFunctionArn =
            requireNotNull(response.headers["Lambda-Runtime-Invoked-Function-Arn"])
        val clientContext = response.headers["Lambda-Runtime-Client-Context"]
        val cognitoIdentity = response.headers["Lambda-Runtime-Cognito-Identity"]
        val xrayTraceId = response.headers["Lambda-Runtime-Trace-Id"]

        return EventContext(
            requestId,
            deadlineMs,
            invokedFunctionArn,
            xrayTraceId,
            clientContext,
            cognitoIdentity,
        )
    }
}

class InvocationEvent(val payload: String, val context: EventContext)

data class EventContext(
    val requestId: String,
    val deadlineMs: Long,
    val invokedFunctionArn: String,
    val xrayTraceId: String?,
    val clientContext: String?,
    val cognitoIdentity: String?
)
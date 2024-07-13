package io.github.trueangle.knative.lambda.runtime.api

import io.github.trueangle.knative.lambda.runtime.api.dto.toDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.ChannelProvider
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ChannelWriterContent
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeStringUtf8
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import platform.posix.fabs
import platform.posix.getenv

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

        println("lambda api respone: ${response.body<String>()}")

        val context = contextFromResponseHeaders(response)
        val body = try {
            response.body(bodyType) as T
        } catch (e: Exception) {
            throw BodyParseException(e, context)
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

            // todo Do we need to set body type directly?
            setBody(body, bodyType)
        }

        return validateResponse(response)
    }

    // todo exceptions, buffer overflow
    // Flow or Channel?
    suspend fun streamResponse(event: Context, streamingBody: Flow<String>): HttpResponse {
        val response = httpClient.post {
            url("${invokeUrl}/invocation/${event.awsRequestId}/response")
            headers {
                if (event.xrayTracingId != null) {
                    append("Lambda-Runtime-Trace-Id", event.xrayTracingId)
                    append("_X_AMZN_TRACE_ID", event.xrayTracingId)
                }

                //append(HttpHeaders.TransferEncoding, "chunked")
                append("Lambda-Runtime-Function-Response-Mode", "streaming")
            }

            setBody(object : OutgoingContent.WriteChannelContent() {
                override suspend fun writeTo(channel: ByteWriteChannel) {
                    /*streamingBody
                        .onCompletion {
                            channel.close(null)
                        }
                        .collect {
                            println("write to stream: $it")
                            channel.writeStringUtf8(it)
                        }

                    channel.close()*/

                    repeat(1024) { i ->
                        channel.writeStringUtf8(i.toString() + "\n")
                        channel.writeStringUtf8("7\r\n")
                        channel.writeStringUtf8("Hello, \r\n")

                        channel.writeStringUtf8("6\r\n")
                        channel.writeStringUtf8("world!\r\n")
                    }

                    channel.writeStringUtf8("0\r\n\r\n") // End of chunks

                    channel.close()
                }
            })

            /*setBody(
                ChannelWriterContent(
                    body = {
                        *//*streamingBody
                            .onCompletion {
                                close(null)
                            }
                            .collect {
                                println("write to stream: $it")
                                writeStringUtf8(it)
                            }*//*
                    },
                    contentType = ContentType.Text.Plain,
                )
            )*/
        }

        return validateResponse(response)
    }

    suspend fun sendError(error: LambdaRuntimeError) {
        val response = when (error) {
            is LambdaRuntimeError.Init -> sendInitError(error)
            is LambdaRuntimeError.Invocation -> sendInvocationError(error)
        }

        validateResponse(response)
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

    //todo handle 403 properly
    private suspend fun validateResponse(response: HttpResponse): HttpResponse {
        if (response.status != HttpStatusCode.Accepted) {
            if (response.status in arrayOf(HttpStatusCode.InternalServerError, HttpStatusCode.BadRequest)) {
                throw NonRecoverableStateException("Non-recoverable state. Response from lambda environment: ${response.body<String>()}")
            }

            throw LambdaClientException("HTTP 202 expected from lambda environment, got status: ${response.status}, error body: ${response.body<String>()}")
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

class LambdaClientException(override val message: String) : IllegalStateException()
class BodyParseException(override val cause: Exception, val context: Context) : IllegalStateException()
class NonRecoverableStateException(override val message: String = "Container error. Non-recoverable state") : IllegalStateException()
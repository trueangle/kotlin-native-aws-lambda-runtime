package io.github.trueangle.knative.lambda.runtime

import com.goncalossilva.resources.Resource
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.spy
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verify.VerifyMode.Companion.order
import dev.mokkery.verifySuspend
import io.github.trueangle.knative.lambda.runtime.LambdaEnvironmentException.*
import io.github.trueangle.knative.lambda.runtime.LambdaRuntimeException.Invocation.EventBodyParseException
import io.github.trueangle.knative.lambda.runtime.LambdaRuntimeException.Invocation.HandlerException
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_NAME
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_VERSION
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_RUNTIME_API
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.api.LambdaClientImpl
import io.github.trueangle.knative.lambda.runtime.events.apigateway.APIGatewayRequest
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.github.trueangle.knative.lambda.runtime.log.LambdaLogger
import io.github.trueangle.knative.lambda.runtime.log.Log
import io.github.trueangle.knative.lambda.runtime.log.LogLevel.ERROR
import io.github.trueangle.knative.lambda.runtime.log.LogLevel.FATAL
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.engine.mock.respondError
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.ChannelWriterContent
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headers
import io.ktor.http.headersOf
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyTo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.serialization.json.Json
import platform.posix.getenv
import platform.posix.setenv
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal const val RESOURCES_PATH = "src/nativeTest/resources"

class LambdaRuntimeTest {
    private val log = spy<LambdaLogger>(Log)
    private val context = Context(
        awsRequestId = "156cb537-e2d4-11e8-9b34-d36013741fb9",
        deadlineTimeInMs = 1542409706888L,
        invokedFunctionArn = "arn",
        clientContext = null,
        cognitoIdentity = null,
        invokedFunctionName = "invokedFunctionName",
        invokedFunctionVersion = "1",
        memoryLimitMb = 128,
        xrayTracingId = null
    )

    @BeforeTest
    fun setup() {
        mockEnvironment()
    }

    @Test
    fun `GIVEN String event WHEN end-to-end THEN behave correctly`() = runTest {
        val lambdaEvent = "Hello world"
        val handlerResponse = "Response"

        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess(lambdaEvent)
                path.contains("/invocation/${context.awsRequestId}/response") -> respond(
                    content = ByteReadChannel("Ok"),
                    status = HttpStatusCode.Accepted,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )

                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client

        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context): String = handlerResponse
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        verifySuspend { handler.handleRequest(lambdaEvent, context) }
        verifySuspend(exactly(1)) { client.retrieveNextEvent<String>(typeInfo<String>()) }
        verifySuspend(exactly(1)) { client.sendResponse(context, handlerResponse, typeInfo<String>()) }
        verify(not) { log.log(ERROR, any<Any>(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN complex object event and response WHEN end-to-end THEN behave correctly`() = runTest {
        val lambdaEventJson = Resource("$RESOURCES_PATH/example-apigw-request.json").readText()
        val apiGatewayRequest = Json.decodeFromString<APIGatewayRequest>(lambdaEventJson)
        val handlerResponse = SampleObject("Hello world")

        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess(lambdaEventJson)
                path.contains("/invocation/${context.awsRequestId}/response") -> respond(
                    content = ByteReadChannel("Ok"),
                    status = HttpStatusCode.Accepted,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )

                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client
        val handler = object : LambdaBufferedHandler<APIGatewayRequest, SampleObject> {
            override suspend fun handleRequest(input: APIGatewayRequest, context: Context) = handlerResponse
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        verifySuspend { handler.handleRequest(apiGatewayRequest, context) }
        verifySuspend(exactly(1)) { client.retrieveNextEvent<APIGatewayRequest>(typeInfo<APIGatewayRequest>()) }
        verifySuspend(exactly(1)) { client.sendResponse(context, handlerResponse, typeInfo<SampleObject>()) }
        verify(not) { log.log(ERROR, any<Any>(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN handler init error WHEN end-to-end THEN terminate immediately AND report init error`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("init/error") -> respond(
                    content = ByteReadChannel(""),
                    status = HttpStatusCode.Accepted,
                )

                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client

        assertFailsWith<TerminateException> {
            lambdaRunner.run(singleEventMode = true) { InitErrorHandler() }
        }
        verifySuspend { client.reportError(any<LambdaRuntimeException.Init.Failed>()) }
        verifySuspend(not) { client.retrieveNextEvent<APIGatewayRequest>(typeInfo<APIGatewayRequest>()) }
        verify(not) { log.log(ERROR, any<Any>(), any()) }
        verify { log.log(FATAL, any<Any>(), any()) }
    }

    @Test
    fun `GIVEN handler init error AND report init failed WHEN end-to-end THEN terminate immediately`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("init/error") -> respondBadRequest()
                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client

        assertFailsWith<TerminateException> {
            lambdaRunner.run(singleEventMode = true) { InitErrorHandler() }
        }
        verifySuspend { client.reportError(any<LambdaRuntimeException.Init.Failed>()) }
        verifySuspend(not) { client.retrieveNextEvent<APIGatewayRequest>(typeInfo<APIGatewayRequest>()) }
        verify(not) { log.log(ERROR, any<Any>(), any()) }
        verify { log.log(FATAL, any<Any>(), any()) }
    }

    @Test
    fun `GIVEN env api error WHEN retrieveNextEvent THEN terminate`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("/invocation/next") -> {
                    respondError(HttpStatusCode.InternalServerError)
                }

                else -> respondError(HttpStatusCode.Forbidden)
            }
        })
        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context) = ""
        }

        assertFailsWith<TerminateException> {
            lambdaRunner.run(singleEventMode = true) { handler }
        }
        verify(not) { log.log(ERROR, any<Any>(), any()) }
        verify { log.log(FATAL, any<Any>(), any()) }
    }

    @Test
    fun `GIVEN bad request from env api WHEN sendResponse THEN skip event`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess("")
                path.contains("${context.awsRequestId}/response") -> respondError(HttpStatusCode.BadGateway)
                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client
        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context) = throw RuntimeException()
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        verifySuspend { client.reportError(any<HandlerException>()) }
        verify(order) { log.log(ERROR, any<HandlerException>(), any()) }
        verify(order) { log.log(ERROR, any<BadRequestException>(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN unknown http error from env api WHEN reportError THEN skip event`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess("")
                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client
        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context) = throw RuntimeException()
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        verifySuspend { client.reportError(any<HandlerException>()) }
        verify(order) { log.log(ERROR, any<HandlerException>(), any()) }
        verify(order) { log.log(ERROR, any<BadRequestException>(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN internal server error from env api WHEN reportError THEN terminate`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess("")
                path.contains("${context.awsRequestId}/error") -> respondError(HttpStatusCode.InternalServerError)
                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client
        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context) = throw RuntimeException()
        }

        assertFailsWith<TerminateException> { lambdaRunner.run(singleEventMode = true) { handler } }
        verifySuspend { client.reportError(any<HandlerException>()) }
        verify { log.log(ERROR, any<HandlerException>(), any()) }
        verify { log.log(FATAL, any<NonRecoverableStateException>(), any()) }
        verify { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN EventBodyParseException WHEN retrieveNextEvent THEN report error AND skip event`() = runTest {
        val event = NonSerialObject("")
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess(event.toString())
                path.contains("${context.awsRequestId}/error") -> respond("", HttpStatusCode.Accepted)
                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client
        val handler = object : LambdaBufferedHandler<NonSerialObject, String> {
            override suspend fun handleRequest(input: NonSerialObject, context: Context) = ""
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        verifySuspend { client.reportError(any<EventBodyParseException>()) }
        verify { log.log(ERROR, any<Any>(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN Handler exception WHEN invoke handler THEN report error AND skip event`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess("")
                path.contains("${context.awsRequestId}/error") -> respond("", HttpStatusCode.Accepted)
                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client
        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context) = throw RuntimeException()
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        verifySuspend { client.reportError(any<HandlerException>()) }
        verify { log.log(ERROR, any<HandlerException>(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @Test
    fun `GIVEN Handler exception WHEN invoke streaming handler THEN consume error`() = runTest {
        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respondNextEventSuccess("")
                path.contains("${context.awsRequestId}/response") -> {
                    println(request.body.contentLength)
                    println(request.body.status)
                    respond("", HttpStatusCode.Accepted)
                }

                else -> respondBadRequest()
            }
        })
        val client = lambdaRunner.client

        val handler = object : LambdaStreamHandler<String, ByteWriteChannel> {
            override suspend fun handleRequest(input: String, output: ByteWriteChannel, context: Context) {
                output.writeMidstreamError(RuntimeException())
                ByteReadChannel("").copyTo(output)
            }
        }

        lambdaRunner.run(singleEventMode = true) { handler }

        //assertTrue(request.body is ByteWriteChannel)

        //val condition = body?.trailers()?.contains(RuntimeException().toTrailer())

        // assertTrue(condition == true)

        verifySuspend { client.streamResponse(any(), any()) }
        verify(not) { log.log(FATAL, any<Any>(), any()) }
        verify(not) { lambdaRunner.env.terminate() }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun mockEnvironment() {
        if (getenv(AWS_LAMBDA_FUNCTION_NAME)?.toKString().isNullOrEmpty()) {
            setenv(AWS_LAMBDA_FUNCTION_NAME, context.invokedFunctionName, 1)
        }

        if (getenv(AWS_LAMBDA_FUNCTION_VERSION)?.toKString().isNullOrEmpty()) {
            setenv(AWS_LAMBDA_FUNCTION_VERSION, context.invokedFunctionVersion, 1)
        }

        if (getenv(AWS_LAMBDA_RUNTIME_API)?.toKString().isNullOrEmpty()) {
            setenv(AWS_LAMBDA_RUNTIME_API, "127.0.0.1", 1)
        }
    }

    private fun createRunner(mockEngine: HttpClientEngine): Runner {
        val client = LambdaClientImpl(LambdaRuntime.createHttpClient(mockEngine))
        val env = mock<LambdaEnvironment> {
            every { terminate() } throws TerminateException()
        }
        val lambdaClient = spy<LambdaClient>(client)
        return Runner(lambdaClient, log, env)
    }

    private fun MockRequestHandleScope.respondNextEventSuccess(lambdaEvent: String) = respond(
        content = ByteReadChannel(lambdaEvent),
        status = HttpStatusCode.OK,
        headers = headers {
            append(HttpHeaders.ContentType, "application/json")
            append("Lambda-Runtime-Aws-Request-Id", context.awsRequestId)
            append("Lambda-Runtime-Deadline-Ms", context.deadlineTimeInMs.toString())
            append("Lambda-Runtime-Invoked-Function-Arn", context.invokedFunctionArn)
        }
    )

    private class InitErrorHandler : LambdaBufferedHandler<String, String> {
        init {
            throw RuntimeException()
        }

        override suspend fun handleRequest(input: String, context: Context) = ""
    }

    private class TerminateException : Error()
}
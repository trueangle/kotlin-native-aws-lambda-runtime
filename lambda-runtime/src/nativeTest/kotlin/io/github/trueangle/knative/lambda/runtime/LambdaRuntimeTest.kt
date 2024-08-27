package io.github.trueangle.knative.lambda.runtime

import com.goncalossilva.resources.Resource
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.spy
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verify.VerifyMode.Companion.not
import dev.mokkery.verifySuspend
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_NAME
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_VERSION
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_RUNTIME_API
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.api.LambdaClientImpl
import io.github.trueangle.knative.lambda.runtime.events.apigateway.APIGatewayRequest
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.log.LambdaLogger
import io.github.trueangle.knative.lambda.runtime.log.LogLevel.ERROR
import io.github.trueangle.knative.lambda.runtime.log.LogLevel.FATAL
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.http.headersOf
import io.ktor.util.reflect.typeInfo
import io.ktor.utils.io.ByteReadChannel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import platform.posix.getenv
import platform.posix.setenv
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

const val RESOURCES_PATH = "src/nativeTest/resources"

class LambdaRuntimeTest {
    private val log = mock<LambdaLogger>()
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
                path.contains("invocation/next") -> respond(
                    content = ByteReadChannel(lambdaEvent),
                    status = HttpStatusCode.OK,
                    headers = headers {
                        append(HttpHeaders.ContentType, "application/json")
                        append("Lambda-Runtime-Aws-Request-Id", context.awsRequestId)
                        append("Lambda-Runtime-Deadline-Ms", context.deadlineTimeInMs.toString())
                        append("Lambda-Runtime-Invoked-Function-Arn", context.invokedFunctionArn)

                    }
                )

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
    }

    @Test
    fun `GIVEN complex object event and response WHEN end-to-end THEN behave correctly`() = runTest {
        val lambdaEventJson = Resource("$RESOURCES_PATH/example-apigw-request.json").readText()
        val apiGatewayRequest = Json.decodeFromString<APIGatewayRequest>(lambdaEventJson)
        val handlerResponse = SampleObject("Hello world")

        val lambdaRunner = createRunner(MockEngine { request ->
            val path = request.url.encodedPath
            when {
                path.contains("invocation/next") -> respond(
                    content = ByteReadChannel(lambdaEventJson),
                    status = HttpStatusCode.OK,
                    headers = headers {
                        append(HttpHeaders.ContentType, "application/json")
                        append("Lambda-Runtime-Aws-Request-Id", context.awsRequestId)
                        append("Lambda-Runtime-Deadline-Ms", context.deadlineTimeInMs.toString())
                        append("Lambda-Runtime-Invoked-Function-Arn", context.invokedFunctionArn)
                    }
                )

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

    private class InitErrorHandler : LambdaBufferedHandler<String, String> {
        init {
            throw RuntimeException()
        }

        override suspend fun handleRequest(input: String, context: Context) = ""
    }

    private class TerminateException : Error()
}
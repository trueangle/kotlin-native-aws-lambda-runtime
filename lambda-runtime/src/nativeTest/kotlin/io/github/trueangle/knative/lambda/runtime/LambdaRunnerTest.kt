package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_NAME
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_FUNCTION_VERSION
import io.github.trueangle.knative.lambda.runtime.ReservedRuntimeEnvironmentVariables.AWS_LAMBDA_RUNTIME_API
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.LambdaClient
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headers
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import platform.posix.getenv
import platform.posix.setenv
import kotlin.test.BeforeClass
import kotlin.test.BeforeTest
import kotlin.test.Test

class LambdaRunnerTest {

    @BeforeTest
    fun setup() {
        mockEnvironment()
    }

    @Test
    fun `GIVEN string event WHEN LambdaBufferedHandler THEN success invocation`() = runTest {
        val handlerResponse = "Response"
        val requestId = "156cb537-e2d4-11e8-9b34-d36013741fb9"
        val deadline = "1542409706888"

        val lambdaRunner = createRunner(MockEngine { request ->
            when {
                request.url.encodedPath.contains("invocation/next") -> respond(
                    content = ByteReadChannel("""Hello world"""),
                    status = HttpStatusCode.OK,
                    headers = headers {
                        append(HttpHeaders.ContentType, "application/json")
                        append("Lambda-Runtime-Aws-Request-Id", requestId)
                        append("Lambda-Runtime-Deadline-Ms", deadline)
                        append("Lambda-Runtime-Invoked-Function-Arn", "arn")

                    }
                )

                else -> respond(
                    content = ByteReadChannel("""{"ip":"127.0.0.1"}"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        })

        val handler = object : LambdaBufferedHandler<String, String> {
            override suspend fun handleRequest(input: String, context: Context): String = handlerResponse
        }

        lambdaRunner.run { handler }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun mockEnvironment() {
        if (getenv(AWS_LAMBDA_FUNCTION_NAME)?.toKString().isNullOrEmpty()) {
            setenv(AWS_LAMBDA_FUNCTION_NAME, "test", 1)
        }

        if (getenv(AWS_LAMBDA_FUNCTION_VERSION)?.toKString().isNullOrEmpty()) {
            setenv(AWS_LAMBDA_FUNCTION_VERSION, "1", 1)
        }

        if (getenv(AWS_LAMBDA_RUNTIME_API)?.toKString().isNullOrEmpty()) {
            setenv(AWS_LAMBDA_RUNTIME_API, "127.0.0.1", 1)
        }
    }

    private fun createRunner(mockEngine: HttpClientEngine): Runner {
        val lambdaClient = LambdaClient(LambdaRuntime.createHttpClient(mockEngine))
        return Runner(lambdaClient)
    }
}
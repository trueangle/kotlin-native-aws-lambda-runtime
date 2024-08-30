package io.github.trueangle.knative.lambda.runtime

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.github.trueangle.knative.lambda.runtime.api.dto.LogMessageDto
import io.github.trueangle.knative.lambda.runtime.log.JsonLogFormatter
import io.github.trueangle.knative.lambda.runtime.log.LogLevel
import io.ktor.util.reflect.typeInfo
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonLogFormatterTest {
    private val clock = mock<Clock>()
    private val requestId = "awsRequestId"
    private val timestamp = Clock.System.now()

    @OptIn(ExperimentalSerializationApi::class)
    private val formatter = JsonLogFormatter(clock = clock, json = Json { explicitNulls = true }).apply {
        onContextAvailable(mockContext(requestId))
    }

    @Test
    fun `GIVEN message of object WHEN format THEN json`() {
        val message = SampleObject("Hello world")

        every { clock.now() } returns (timestamp)

        val expected = Json.encodeToString(
            LogMessageDto(
                timestamp = timestamp.toString(),
                message = message,
                level = LogLevel.INFO,
                awsRequestId = requestId
            )
        )
        val actual = formatter.format(LogLevel.INFO, message, typeInfo<SampleObject>())

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN message of primitive WHEN format THEN json`() {
        val message = "Hello world"

        every { clock.now() } returns (timestamp)

        val expected = Json.encodeToString(
            LogMessageDto(
                timestamp = timestamp.toString(),
                message = message,
                level = LogLevel.INFO,
                awsRequestId = requestId
            )
        )
        val actual = formatter.format(LogLevel.INFO, message, typeInfo<String>())

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN non-serializable message object WHEN format THEN json`() {
        val message = NonSerialObject("Hello world")

        every { clock.now() } returns (timestamp)

        val expected = Json.encodeToString(
            LogMessageDto(
                timestamp = timestamp.toString(),
                message = message.toString(),
                level = LogLevel.INFO,
                awsRequestId = requestId
            )
        )
        val actual = formatter.format(LogLevel.INFO, message, typeInfo<NonSerialObject>())

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN null message WHEN format THEN json`() {
        val message = null

        every { clock.now() } returns (timestamp)

        val expected = Json.encodeToString(
            LogMessageDto(
                timestamp = timestamp.toString(),
                message = null,
                level = LogLevel.INFO,
                awsRequestId = requestId
            )
        )
        val actual = formatter.format(LogLevel.INFO, message, typeInfo<NonSerialObject>())

        assertEquals(expected, actual)
    }

    @Test
    fun `GIVEN throwable message WHEN format THEN json`() {
        val message = RuntimeException("Runtime exception occurred")

        every { clock.now() } returns (timestamp)

        val expected = Json.encodeToString(
            LogMessageDto(
                timestamp = timestamp.toString(),
                message = message.asSerialObject(),
                level = LogLevel.INFO,
                awsRequestId = requestId
            )
        )
        val actual = formatter.format(LogLevel.INFO, message, typeInfo<RuntimeException>())

        assertEquals(expected, actual)
    }
}
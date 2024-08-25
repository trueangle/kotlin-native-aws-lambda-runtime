package io.github.trueangle.knative.lambda.runtime.log

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.api.dto.LogMessageDto
import kotlinx.datetime.Clock
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
internal class JsonLogFormatter : LogFormatter {
    private var requestContext: Context? = null

    override fun format(logLevel: LogLevel, message: Any?): String {
        val json = try {
            Json.encodeToString(
                LogMessageDto(
                    timestamp = Clock.System.now().toString(),
                    message = if (message is Throwable) message.prettyPrint() else message?.let {
                        it::class.serializer()
                    },
                    level = logLevel,
                    awsRequestId = requestContext?.awsRequestId
                )
            )
        } catch (e: SerializationException) {
            Log.warn("Log serialisation error: ${e.message}")

            Json.encodeToString(
                LogMessageDto(
                    timestamp = Clock.System.now().toString(),
                    message = message?.toString(),
                    level = logLevel,
                    awsRequestId = requestContext?.awsRequestId
                )
            )
        }

        return json
    }

    override fun onContextAvailable(context: Context) {
        requestContext = context
    }
}
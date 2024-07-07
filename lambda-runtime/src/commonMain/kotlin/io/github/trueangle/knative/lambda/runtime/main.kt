package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun main() = LambdaRuntime.run { SampleLambdaHandler() }

fun mainStreaming() = LambdaRuntime.run { SampleStreamingHandler() }

class SampleLambdaHandler : LambdaHandler<String, String> {
    override fun handleRequest(input: String, context: Context): String {
        println("Invoke lambda handler\n payload: $input\n context: $context")

        return """ {
                  "statusCode": 200,
                  "headers": {
                    "Content-Type": "application/json"
                  },
                  "isBase64Encoded": false,
                  "body": "Hello world"
              }
    """.trimIndent()
    }

}

class SampleStreamingHandler : LambdaStreamHandler {
    override fun handleRequest(input: ByteArray, context: Context): Flow<ByteArray> {
        return flow {

        }
    }
}

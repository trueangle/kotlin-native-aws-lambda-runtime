package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.Context

fun main() = LambdaRuntime.run()

class SampleLambda : LambdaHandler {
    override fun handleRequest(payload: String, context: Context): String {
        println("Invoke lambda handler\n payload: $payload\n context: $context")

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

package io.github.trueangle.knative.lambda.runtime

fun main() = LambdaRuntime.run(SampleLambda())

private class SampleLambda : LambdaHandler {
    override fun handleRequest(payload: String, context: EventContext): String {
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

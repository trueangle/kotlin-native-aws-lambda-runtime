package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler

class StringBodyLambdaHandler : LambdaBufferedHandler<String, String> {
    override suspend fun handleRequest(input: String, context: Context): String {
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
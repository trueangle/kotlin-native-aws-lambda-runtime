package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaBufferedHandler
import io.github.trueangle.knative.lambda.runtime.handler.LambdaHandler
import io.github.trueangle.knative.lambda.runtime.log.Log
import io.ktor.utils.io.core.toByteArray

class ByteBodyLambdaHandler : LambdaBufferedHandler<ByteArray, ByteArray> {
    override suspend fun handleRequest(input: ByteArray, context: Context): ByteArray {
        Log.debug("Invoke lambda handler\n payload: $input\n context: $context")

        return """ {
                  "statusCode": 200,
                  "headers": {
                    "Content-Type": "application/json"
                  },
                  "isBase64Encoded": false,
                  "body": "Hello world"
              }
    """.trimIndent().toByteArray()
    }

}
package io.github.trueangle.knative.lambda.runtime

import io.github.trueangle.knative.lambda.runtime.api.Context

interface LambdaHandler {
    fun handleRequest(payload: String, context: Context): String
}
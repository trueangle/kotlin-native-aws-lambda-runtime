package io.github.trueangle.knative.lambda.runtime

interface LambdaHandler {
    fun handleRequest(payload: String, context: EventContext): String
}
package io.github.trueangle.knative.lambda.runtime.handler

import io.github.trueangle.knative.lambda.runtime.api.Context

interface LambdaHandler {
    fun handleRequest(payload: String, context: Context): String
}

/*
interface LambdaHandler<I, O> {
    fun handleRequest(input: I, context: Context): O
}

interface LambdaStreamHandler: LambdaHandler<ByteArray, ByteArray>*/

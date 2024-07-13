package io.github.trueangle.knative.lambda.runtime.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import kotlinx.coroutines.flow.Flow

interface LambdaHandler<I, O> {
    fun handleRequest(input: I, context: Context): O
}

interface LambdaStreamHandler : LambdaHandler<ByteArray, Flow<String>>

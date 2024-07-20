package io.github.trueangle.knative.lambda.runtime.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.ktor.utils.io.ByteWriteChannel

interface LambdaHandler<I, O>

interface LambdaBufferedHandler<I, O> : LambdaHandler<I, O> {
    suspend fun handleRequest(input: I, context: Context): O
}

interface LambdaStreamHandler<I, O: ByteWriteChannel> : LambdaHandler<I, ByteWriteChannel> {
    suspend fun handleRequest(input: I, output: ByteWriteChannel, context: Context)
}

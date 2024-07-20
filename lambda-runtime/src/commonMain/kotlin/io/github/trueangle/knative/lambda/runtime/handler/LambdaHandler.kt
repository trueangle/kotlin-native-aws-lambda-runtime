package io.github.trueangle.knative.lambda.runtime.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.flow.Flow

interface LambdaHandler<I, O> {
    suspend fun handleRequest(input: I, context: Context): O
}

interface LambdaStreamHandler : LambdaHandler<ByteArray, ByteReadChannel>

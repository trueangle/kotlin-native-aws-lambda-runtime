package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SampleStreamingHandler : LambdaStreamHandler {
    override fun handleRequest(input: ByteArray, context: Context): Flow<String> {
        return flow {
            repeat(2024) { i ->
                emit(i.toString() + "\n")
                delay(10)
            }
        }
    }
}

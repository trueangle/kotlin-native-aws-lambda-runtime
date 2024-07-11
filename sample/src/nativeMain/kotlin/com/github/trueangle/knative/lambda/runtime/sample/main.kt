package com.github.trueangle.knative.lambda.runtime.sample

import com.github.trueangle.knative.lambda.runtime.sample.handler.ByteBodyLambdaHandler
import io.github.trueangle.knative.lambda.runtime.LambdaRuntime
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun main() = LambdaRuntime.run { ByteBodyLambdaHandler() }

class SampleStreamingHandler : LambdaStreamHandler {
    override fun handleRequest(input: ByteArray, context: Context): Flow<ByteArray> {
        return flow {

        }
    }
}

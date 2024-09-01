package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyTo
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class SampleStreamingHandler : LambdaStreamHandler<ByteArray, ByteWriteChannel> {
    override suspend fun handleRequest(input: ByteArray, output: ByteWriteChannel, context: Context) {
        ByteReadChannel(SystemFileSystem.source(Path("o.json")).buffered()).copyTo(output)
    }
}

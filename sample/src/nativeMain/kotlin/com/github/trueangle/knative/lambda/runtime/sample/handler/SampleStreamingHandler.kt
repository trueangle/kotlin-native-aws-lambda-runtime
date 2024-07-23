package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.read
import io.ktor.utils.io.readFully
import io.ktor.utils.io.reader
import io.ktor.utils.io.write
import io.ktor.utils.io.writeSource
import io.ktor.utils.io.writeStringUtf8
import io.ktor.utils.io.writer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class SampleStreamingHandler : LambdaStreamHandler<ByteArray, ByteWriteChannel> {
    override suspend fun handleRequest(input: ByteArray, output: ByteWriteChannel, context: Context) {
        ByteReadChannel(SystemFileSystem.source(Path("o.json")).buffered()).copyTo(output)
    }
}
package com.github.trueangle.knative.lambda.runtime.sample.handler

import io.github.trueangle.knative.lambda.runtime.api.Context
import io.github.trueangle.knative.lambda.runtime.handler.LambdaStreamHandler
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.writeSource
import io.ktor.utils.io.writer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class SampleStreamingHandler : LambdaStreamHandler<ByteArray> {
    override suspend fun handleRequest(input: ByteArray, context: Context): ByteReadChannel {

        //val fileChanel = ByteReadChannel(SystemFileSystem.source(Path("o.json")).buffered())

        /*
                println("create channel")
                val outputChannel = ByteChannel(true)
                ByteReadChannel("asdasdads").copyTo(outputChannel)
        */
        println("create channel")
        /*    return ByteChannel(true).apply {
                //writeSource(SystemFileSystem.source(Path("o.json")).buffered())

            }*/

        /*    GlobalScope.writer(currentCoroutineContext(), true) {
                channel.writeSource(SystemFileSystem.source(Path("o.json")).buffered())
            }.channel*/

        //return ByteReadChannel(SystemFileSystem.source(Path("o.json")).buffered())

        return GlobalScope.writer(currentCoroutineContext(), true) {
            channel.writeSource(SystemFileSystem.source(Path("o.json")).buffered())
        }.channel
    }
}

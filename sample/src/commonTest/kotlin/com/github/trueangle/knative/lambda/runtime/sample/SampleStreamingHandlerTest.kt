package com.github.trueangle.knative.lambda.runtime.sample

import com.github.trueangle.knative.lambda.runtime.sample.handler.SampleStreamingHandler
import io.github.trueangle.knative.lambda.runtime.api.Context
import io.ktor.utils.io.read
import io.ktor.utils.io.readBuffer
import kotlinx.coroutines.test.runTest
import kotlinx.io.readString
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleStreamingHandlerTest {
    val handler = SampleStreamingHandler()

  /*  @Test
    fun test() = runTest {
        val byteChannel = handler.handleRequest(ByteArray(1), Context("asd", "asd", 100, "", null, null))
        assertEquals("", byteChannel.readBuffer().readString())
    }*/
}
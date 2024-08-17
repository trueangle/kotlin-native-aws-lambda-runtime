package com.github.trueangle.knative.lambda.runtime.sample

import com.github.trueangle.knative.lambda.runtime.sample.handler.StringBodyLambdaHandler
import io.github.trueangle.knative.lambda.runtime.LambdaRuntime

fun main() = LambdaRuntime.run { StringBodyLambdaHandler() }
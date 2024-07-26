package io.github.trueangle.knative.lambda.runtime.log

class StdoutLogWriter : LogWriter {
    override fun write(level: LogLevel, message: Any?) = println(message)
}
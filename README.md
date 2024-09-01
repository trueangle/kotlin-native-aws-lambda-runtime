# Kotlin Native Runtime for AWS Lambda

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.trueangle/lambda-runtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.trueangle/lambda-runtime/badge.svg)

A runtime for executing AWS Lambda Functions powered by Kotlin Native, designed to mitigate known
cold start issues associated with the JVM platform.

Project structure:

- `lambda-runtime` — is a library that provides a Lambda runtime.
- `lambda-events` — is a library with strongly-typed Lambda event models, such
  as `APIGatewayRequest`, `DynamoDBEvent`, `S3Event`, `KafkaEvent`, `SQSEvent` and so on.

The runtime supports the
following [OS-only runtime machines](https://docs.aws.amazon.com/lambda/latest/dg/lambda-runtimes.html):

- Amazon Linux 2023 (provided.al2023) with x86_64 architecture
- Amazon Linux 2 (provided.al2) with x86_64 architecture

## Performance

Performance benchmarks reveal that Kotlin Native's "Hello World" Lambda function, executed on Amazon
Linux 2023 (x86_64) with 1024MB of memory, ranks among the top 5 fastest cold starts. Its
performance is on par with Python and .NET implementations. For a comparison with other languages (
including Java), visit https://maxday.github.io/lambda-perf/.

![Kotlin Native AWS Lambda Runtime benchmarks](docs/performance_hello_world.png)

## Getting started

If you have never used AWS Lambda before, check out this getting started guide. 

To create a simple lambda function, follow the following steps:
1. Create Kotlin multiplatform project
2. Include library dependency into your module-level build.gradle file
```kotlin
//..
kotlin {
    //..
    sourceSets {
        nativeMain.dependencies {
            implementation("io.github.trueangle:lambda-runtime:0.0.1") 
            implementation("io.github.trueangle:lambda-events:0.0.1")
        }
    }
    //..
}
//..
```
3. Specify application entry point reference and supported targets
```kotlin
//..
kotlin {
    //..
    listOf(
        macosArm64(),
        macosX64(),
        linuxArm64(),
        linuxX64(),
    ).forEach {
        it.binaries {
            executable {
                entryPoint = "com.github.trueangle.knative.lambda.runtime.sample.main"
            }
        }
    }
    //..
}
//..
```
4. Choose lambda function type.

There are two types of lambda functions:

**Buffered** functions process incoming events by first collecting them
into a buffer before execution. This is a default behavior.

```kotlin
class HelloWorldLambdaHandler : LambdaBufferedHandler<APIGatewayV2Request, APIGatewayV2Response> {
    override suspend fun handleRequest(input: APIGatewayV2Request, context: Context): APIGatewayV2Response {
        return APIGatewayV2Response(
            statusCode = 200,
            body = "Hello world",
            cookies = null,
            headers = null,
            isBase64Encoded = false
        )
    }
}
```

**Streaming** functions, on the other hand, process events in real-time as they arrive, without any
intermediate buffering. This method is well-suited for use cases requiring immediate data
processing, such as real-time analytics or event-driven architectures where low-latency responses
are crucial.

```kotlin
class SampleStreamingHandler : LambdaStreamHandler<ByteArray, ByteWriteChannel> {
    override suspend fun handleRequest(input: ByteArray, output: ByteWriteChannel, context: Context) {
        ByteReadChannel(SystemFileSystem.source(Path("hello.json")).buffered()).copyTo(output)
    }
}
```

5. Specify application entry point using standard `main` and `LambdaRuntime.run` functions
```kotlin
fun main() = LambdaRuntime.run { HelloWorldLambdaHandler() } // or SampleStreamingHandler for streaming lambda
```

## Testing Runtime locally

To run local runtime
locally [aws runtime emulator](https://github.com/aws/aws-lambda-runtime-interface-emulator) is
used:

1. `./gradlew build` to build lambda executable
2. Modify runtime-emulator/Dockerfile to set proper path to the generated executable (.kexe) file,
   located in build/bin/linuxX64/releaseExecutable
3. Run `docker build -t sample:latest .`
4. Start server `docker run -p 9000:8080 sample:latest`
5. Execute function
   via `curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{}'`
6. `docker ps; docker stop CONTAINER_ID` to stop the execution

## Build and deploy to AWS

## Logging

The Runtime uses AWS logging conventions for enhanced log capture, supporting String and JSON log
output
format. It also allows you to dynamically control log levels without altering your code, simplifying
the debugging process. Additionally, you can direct logs to specific Amazon CloudWatch log groups,
making log management and aggregation more efficient at scale. More details on how to set log format
and level refer to the article.
https://aws.amazon.com/blogs/compute/introducing-advanced-logging-controls-for-aws-lambda-functions/

To log lambda function code, use the global Log object with extension functions. The log message
accepts any object / primitive type.

```
Log.trace(message: T?) // The most fine-grained information used to trace the path of your code's execution

Log.debug(message: T?) // Detailed information for system debugging

Log.info(message: T?) // Messages that record the normal operation of your function

Log.warn(message: T?) // Messages about potential errors that may lead to unexpected behavior if unaddressed

Log.error(message: T?) // Messages about problems that prevent the code from performing as expected

Log.fatal(message: T?) // Messages about serious errors that cause the application to stop functioning
```

## Troubleshoot

- If you're going to use Amazon Linux 2023 machine, you'll need to create
  a [lambda layer](https://docs.aws.amazon.com/lambda/latest/dg/chapter-layers.html) with
  libcrypt.so dependency. This is a dynamic library and seems not included into Amazon Linux 2023
  container. The lybcrypt.so can be taken directly from your linux machine (e.g. from
  /lib/x86_64-linux-gnu/libcrypt.so.1 ) or via the
  following [Github Action workflow](https://github.com/trueangle/kotlin-native-aws-lambda-runtime/actions/workflows/libcrypt.yml).
  Once retrieved, zip it and upload as a layer to your lambda function.

- For the time being, only x86-64 architecture is supported by the runtime. LinuxArm64 is not
  supported by Kotlin Native still, details:
    1. The list of supported targets for Kotlin Native (
       2.0.20-RC2) https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-native-prebuilt/2.0.20-RC2/
    2. Opened
       issue: https://youtrack.jetbrains.com/issue/KT-36871/Support-Aarch64-Linux-as-a-host-for-the-Kotlin-Native
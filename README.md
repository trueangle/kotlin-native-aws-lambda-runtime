# Kotlin Native Runtime for AWS Lambda
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.trueangle/lambda-runtime/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.trueangle/lambda-runtime/badge.svg)

A runtime for executing AWS Lambda Functions powered by Kotlin Native, designed to mitigate known
cold start issues associated with the JVM platform.

Project structure:

- lambda-runtime — is a library that provides a Lambda runtime.
- lambda-events — is a library with strongly-typed Lambda event models, such
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

![screenshot](docs/performance_hello_world.png)
<img src="" alt="Kotlin Native AWS Lambda Runtime benchmarks" width="800"/>
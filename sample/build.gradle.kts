plugins {
    alias(libs.plugins.ktor)
    application
}

group = "com.github.trueangle.knative.lambda.runtime.sample"
version = "1.0.0"
application {
    mainClass.set("com.github.trueangle.knative.lambda.runtime.sample.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
}
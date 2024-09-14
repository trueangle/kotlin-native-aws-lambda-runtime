plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(17)
}

group = "io.github.trueangle.plugin.lambda" 
version = "0.0.1" 

gradlePlugin {
    website.set("https://github.com/trueangle/kotlin-native-aws-lambda-runtime")
    vcsUrl.set("https://github.com/trueangle/kotlin-native-aws-lambda-runtime.git")

    plugins {
        create("buildLambdaRelease") {
            id = "io.github.trueangle.plugin.lambda"
            implementationClass = "LambdaPlugin"
            displayName = "A plugin for Kotlin Native AWS Lambda"
            description = "A plugin to streamline the development of Kotlin Native AWS Lambda functions"
            tags.set(listOf("aws", "lambda", "kotlin-native"))
        }
    }
}
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("io.github.trueangle.plugin.lambda")
}

kotlin {
    listOf(
        macosArm64(),
        macosX64(),
        //linuxArm64(), // https://youtrack.jetbrains.com/issue/KT-36871/Support-Aarch64-Linux-as-a-host-for-the-Kotlin-Native
        linuxX64(),
    ).forEach {
        it.binaries {
            executable {
                entryPoint = "com.github.trueangle.knative.lambda.runtime.sample.main"
            }
        }
    }

    sourceSets {
        nativeMain.dependencies {
            implementation(projects.lambdaRuntime)
            implementation(projects.lambdaEvents)
            implementation(libs.kotlin.serialization.json)
        }
    }
}

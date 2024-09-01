plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
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

    sourceSets {
        nativeMain.dependencies {
            implementation(projects.lambdaRuntime)
            implementation(projects.lambdaEvents)
            implementation(libs.kotlin.serialization.json)
        }
    }
}

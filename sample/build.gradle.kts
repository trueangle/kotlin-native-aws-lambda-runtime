plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("io.github.trueangle.plugin.lambda")
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
                freeCompilerArgs += listOf("-Xallocator=std")
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

buildLambdaRelease {
    architecture.set(Architecture.LINUX_X64) // or Architecture.LINUX_ARM64
}
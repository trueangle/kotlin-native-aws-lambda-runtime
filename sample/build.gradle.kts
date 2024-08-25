plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

// todo is it a right way to specify main entry point for linux64?
kotlin {
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> if(isArm64) macosArm64() else macosX64()
        hostOs == "Linux" -> if (isArm64) linuxArm64() else linuxX64()
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
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

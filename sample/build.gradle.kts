plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

// todo is it a right way to specify main entry point for linux64?
kotlin {

    listOf(
        linuxX64()
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
        }
    }
}

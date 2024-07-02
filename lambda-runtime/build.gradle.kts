plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    linuxX64 {
        binaries {
            executable {
                entryPoint = "io.github.trueangle.knative.lambda.runtime.MainKt.main"
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
        }

        nativeMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

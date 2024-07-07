plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
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
            implementation(libs.kotlin.serialization.json)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.content.json)
        }

        nativeMain.dependencies {
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

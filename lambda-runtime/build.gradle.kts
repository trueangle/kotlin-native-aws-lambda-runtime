plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val nativeTarget = if (isArm64) linuxArm64() else linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.io.core)
            implementation(libs.kotlin.date.time)
            //implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.curl)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.content.json)
        }

        nativeMain.dependencies {}

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val nativeTarget = if (isArm64) linuxArm64() else linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

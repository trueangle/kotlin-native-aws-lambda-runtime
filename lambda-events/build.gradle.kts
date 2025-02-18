plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.resources)
    id("convention.publication")
}

kotlin {
    macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.serialization.json)
        }

        val nativeTest by creating {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.resources)
            }
        }
    }
}
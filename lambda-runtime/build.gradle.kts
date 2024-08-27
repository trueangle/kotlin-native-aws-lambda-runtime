import dev.mokkery.MockMode

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlinx.resources)
}

kotlin {
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> if (isArm64) macosArm64() else macosX64()
        hostOs == "Linux" -> if (isArm64) linuxArm64() else linuxX64()
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.io.core)
            implementation(libs.kotlin.date.time)
            implementation(libs.ktor.client.curl)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.content.json)
        }

        nativeTest.dependencies {
            implementation(projects.lambdaEvents)
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.resources)
        }
    }
}

mokkery {
    defaultMockMode.set(MockMode.autoUnit)
}

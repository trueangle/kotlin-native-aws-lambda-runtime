import dev.mokkery.MockMode
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlinx.resources)
    id("convention.publication")
}

kotlin {
    listOf(
        macosArm64(),
        macosX64(),
        //linuxArm64() // https://youtrack.jetbrains.com/issue/KT-36871/Support-Aarch64-Linux-as-a-host-for-the-Kotlin-Native
        linuxX64(),
    ).forEach {
        it.binaries.all {
            freeCompilerArgs = freeCompilerArgs + listOf("-Xallocator=std", "-Xruntime-logs=gc=info",)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.lambdaEvents)
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

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

// todo is it a right way to specify main entry point for linux64?
kotlin {
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val nativeTarget = if (isArm64) linuxArm64() else linuxX64()

    nativeTarget.apply {
        /*compilations.getByName("main") {
            cinterops {
                val libcurl by creating {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/libcurl.def"))
               *//*     packageName("com.github.trueangle.knative.lambda.runtime.sample.libcurl")
                    compilerOpts("-I/path")
                    includeDirs.allHeaders("path")*//*
                }
            }
        }*/
        binaries {
            executable {
                //linkerOpts = mutableListOf("-L/opt/homebrew/Cellar/curl/8.9.1/lib", "-I/opt/homebrew/Cellar/curl/8.9.1/include", "-lcurl")
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

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}

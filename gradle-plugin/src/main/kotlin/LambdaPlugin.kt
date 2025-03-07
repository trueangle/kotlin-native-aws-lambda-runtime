import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import java.io.File

class LambdaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            val buildLambdaRelease = extensions.create(
                "buildLambdaRelease",
                LambdaPackagerExtension::class.java,
                project
            )
            tasks.apply {
                register("buildLambdaRelease", LambdaPackagerTask::class.java).configure {
                    architecture.set(buildLambdaRelease.architecture)
                }
            }
        }
    }
}

enum class Architecture(val path: String) {
    LINUX_X64("linuxX64"),
    LINUX_ARM64("linuxArm64");

    override fun toString(): String = path
}

/**
 * Gradle project level extension object definition for the LambdaPackagerTask
 *
 */
open class LambdaPackagerExtension(project: Project) {
    /**
     * The architecture of the desired Lambda runtime, default: LINUX_X64
     */
    val architecture = project.objects.property<Architecture>()

    init {
        architecture.set(Architecture.LINUX_X64)
    }
}

abstract class LambdaPackagerTask : DefaultTask() {
    @Input
    val architecture: Property<Architecture> =
        project.objects.property(Architecture::class.java).convention(Architecture.LINUX_X64)

    @TaskAction
    fun packageLambda() {
        project.exec {
            workingDir = project.rootDir
            commandLine("./gradlew", "build")
        }

        val buildDir = project.layout.buildDirectory
        val executableDir = project.file(buildDir.dir("bin/${architecture.get()}/releaseExecutable"))
        val executable = executableDir
            .listFiles()
            ?.first { it.name.endsWith(".kexe") }
            ?: throw IllegalStateException("No .kexe file found in $executableDir")
        val outputZip = project.file(buildDir.dir("lambda/release/${executable.nameWithoutExtension}.zip"))

        outputZip.parentFile.mkdirs()
        outputZip.setWritable(true)

        val bootstrapFile = File(executableDir, "bootstrap")
        bootstrapFile.writeText(
            """
            #!/bin/sh
            ./"${'$'}_HANDLER"
            """.trimIndent()
        )
        bootstrapFile.setExecutable(true)

        project.exec {
            workingDir = executableDir
            commandLine("zip", "-r", outputZip.absolutePath, ".")
        }

        logger.lifecycle(
            "\u001B[0;32mLambda package created at ${outputZip.absolutePath}. " +
                    "Provide the \u001B[1m${executable.name}\u001B[0;32m as a value of " +
                    "\u001B[1m--handler\u001B[0;32m argument while configuring the lambda function on AWS\u001B[0m\n"
        )
    }
}
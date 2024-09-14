import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

class LambdaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("buildLambdaRelease", LambdaPackagerTask::class.java)
    }
}

abstract class LambdaPackagerTask : DefaultTask() {

    @TaskAction
    fun packageLambda() {
        project.exec {
            workingDir = project.rootDir
            commandLine("./gradlew", "build")
        }

        val buildDir = project.layout.buildDirectory
        val executableDir = project.file(buildDir.dir("bin/linuxX64/releaseExecutable"))
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
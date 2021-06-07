package top.abosen.plugins.changelog

import org.gradle.api.internal.project.DefaultProject
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files.createTempDirectory
import kotlin.test.BeforeTest

/**
 * @author qiubaisen
 * @date 2021/6/7
 */
open class BaseTest {

    protected lateinit var project: DefaultProject
    protected lateinit var extension: ChangelogPluginExtension

    protected var version: String
        get() = project.version.toString()
        set(value) {
            project.version = value
        }

    protected var buildFile = ""
        set(value) {
            field = value
            File("${project.projectDir}/build.gradle").run {
                if (exists()) {
                    delete()
                }
                createNewFile()
                writeText(value.trimIndent())
            }
        }

    protected var changelog: String = ""
        set(value) {
            field = value
            File(extension.path).run {
                if (exists()) {
                    delete()
                }
                createNewFile()
                writeText(value.trimIndent().trim())
            }
        }

    @BeforeTest
    fun setUp() {
        project = ProjectBuilder.builder()
            .withName("project")
            .withProjectDir(createTempDirectory("temp").toFile()).build() as DefaultProject

        project.version = "1.0.0"
        project.plugins.apply(ChangelogPlugin::class.java)
        extension = project.extensions.getByType(ChangelogPluginExtension::class.java)

    }

    private fun prepareTask(taskName: String, vararg arguments: String) =
        GradleRunner.create()
            .withProjectDir(project.projectDir)
            .withArguments(taskName, "--console=plain", "--stacktrace", *arguments)
            .withPluginClasspath()

    protected fun runTask(taskName: String, vararg arguments: String): BuildResult =
        prepareTask(taskName, *arguments).build()

    protected fun runFailingTask(taskName: String, vararg arguments: String): BuildResult =
        prepareTask(taskName, *arguments).buildAndFail()
}

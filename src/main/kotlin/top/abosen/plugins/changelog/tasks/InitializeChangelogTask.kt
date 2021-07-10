package top.abosen.plugins.changelog.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import top.abosen.plugins.changelog.ChangelogPluginExtension
import java.io.File

/**
 * @author qiubaisen
 * @date 2021/6/7
 */
open class InitializeChangelogTask : DefaultTask() {
    private val extension:ChangelogPluginExtension = project.extensions.getByType(ChangelogPluginExtension::class.java)

    @TaskAction
    fun run() {
        File(extension.path).apply {
            if (!exists()) {
                createNewFile()
            }
        }.writeText(
            """
             # Changelog
                
                ## ${extension.unreleasedTerm}
                ### ${extension.groups.first()}
                ${extension.itemPrefix} Example item   
            """.trimIndent() + extension.groups.drop(1).joinToString("\n") { "### $it\n" }
        )
    }
}

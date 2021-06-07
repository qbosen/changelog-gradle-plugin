package top.abosen.plugins.changelog

import org.gradle.api.Plugin
import org.gradle.api.Project
import top.abosen.plugins.changelog.tasks.GetChangelogTask
import top.abosen.plugins.changelog.tasks.InitializeChangelogTask

/**
 * @author qiubaisen
 * @date 2021/6/7
 */
class ChangelogPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            extensions.create("changelog", ChangelogPluginExtension::class.java, objects, projectDir)
            tasks.apply {
                create("initializeChangelog", InitializeChangelogTask::class.java) {
                    it.group = "changelog"
                }
                create("getChangelog", GetChangelogTask::class.java) {
                    it.group = "changelog"
                    it.outputs.upToDateWhen { false }
                }
            }
        }
    }
}

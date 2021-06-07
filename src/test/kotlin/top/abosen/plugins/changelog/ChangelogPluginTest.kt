package top.abosen.plugins.changelog

import kotlin.test.Test

/**
 * @author qiubaisen
 * @date 2021/6/7
 */
class ChangelogPluginTest : BaseTest() {
    @Test
    fun `creates new changelog file`() {
        buildFile =
            """
            plugins {
                id 'top.abosen.plugins.changelog'
            }
            changelog {
                version = "1.0.0"
            }
            """
        project.evaluate()

        runTask("initializeChangelog")
    }
}

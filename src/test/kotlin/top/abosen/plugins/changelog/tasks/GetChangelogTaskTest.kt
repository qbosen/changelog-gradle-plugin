package top.abosen.plugins.changelog.tasks

import top.abosen.plugins.changelog.BaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author  qiubaisen
 * @date  2021/6/7
 */
class GetChangelogTaskTest : BaseTest() {
    @BeforeTest
    fun localSetUp() {
        changelog =
            """
            # Changelog
            ## [Unreleased]
            - bar
            ## [1.0.0]
            ### Added
            - foo
            """

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
    }

    @Test
    fun `returns change notes for the version specified with extension`() {
        val result = runTask("getChangelog", "--quiet")
        assertEquals(
            """
            ## [1.0.0]
            ### Added
            - foo
            """.trimIndent(),
            result.output.trim()
        )
    }
}
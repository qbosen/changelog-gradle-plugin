package top.abosen.plugins.changelog

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import top.abosen.plugins.changelog.exceptions.HeaderParseException
import kotlin.test.*

/**
 * @author qiubaisen
 * @date 2021/7/4
 */
class ChangelogParseTest {
    lateinit var tree: ASTNode
    lateinit var content: String

    @BeforeTest
    fun load() {
        content = """
            # Changelog
            Changelog parse test
            ## [Unreleased]
            ### Added
            - unreleased added
            ### Changed
            - unreleased changed
            ## [1.0.0] 2021-07-01
            ### Added
            - multiple
            line
            ### Fixed
            - 1_0_0 fixed
            ## [0.9.1-alpha.1]
            ### Added
            - 0_9_1 added
        """.trimIndent()
        val flavour = GFMFlavourDescriptor()
        val parser = MarkdownParser(flavour)
        tree = parser.buildMarkdownTreeFromString(content)
    }

    private fun ASTNode.text() = getTextInNode(content).toString()

    private fun List<ASTNode>.groupByType(
        type: IElementType,
        getKey: ((item: ASTNode) -> String)? = null
    ): Map<String, List<ASTNode>> {
        var key = ""
        return groupBy {
            if (it.type == type) {  // 开始一个新的key
                key = getKey?.invoke(it) ?: it.text()
            }
            key
        }
    }

    @Test
    fun `按节点类型分组`() {
        val groupResult = tree.children.groupByType(MarkdownElementTypes.ATX_2) {
            assertEquals(2, it.children.size, "it是每一个head2节点, 有 ## 和 body 两部分")
            assertEquals("##", it.children.first().text(), "第一部分是 ##")
            // key 是 ## 后面的内容
            it.children.last().text().trim()
        }

        assertEquals(
            "# Changelog\nChangelog parse test\n",
            groupResult[""]!!.joinToString("") { it.text() },
            "key为空串的是第一个head2之前的内容"
        )

        // 测试所有key
        assertContentEquals(
            listOf("", "[Unreleased]", "[1.0.0] 2021-07-01", "[0.9.1-alpha.1]"),
            groupResult.keys
        )
    }

    @Test
    fun `测试版本header解析`() {
        val headerParserRegex = Version.semVerRegex
        fun parse(headerLine: String) = headerLine.split("""[^-+.0-9a-zA-Z]+""".toRegex())
            .firstOrNull(headerParserRegex::matches)

        assertEquals("1.0.0", parse("[1.0.0] 2021-07-01"))
        assertEquals("1.0.0-alpha.1", parse("[1.0.0-alpha.1] 版本备注"))
        assertEquals("1.0.0+20130313144700", parse("[1.0.0+20130313144700]"))
    }

    @Test
    fun `测试解析h3`() {
        val unreleasedMark = "[Unreleased]"
        val h2versionMaps: Map<String, List<ASTNode>> = tree.children.groupByType(MarkdownElementTypes.ATX_2) {
            it.children.last().text().trim().run {
                when (this) {
                    unreleasedMark -> this
                    else -> split("""[^-+.0-9a-zA-Z]+""".toRegex())
                        .firstOrNull(Version.semVerRegex::matches) ?: throw HeaderParseException(this)
                }
            }
        }.filterKeys(String::isNotEmpty)

        val h3versionMaps: Map<String, Map<String, List<ASTNode>>> = h2versionMaps.mapValues { (key, value) ->
            // group的第一行是 h2本身
            assertTrue { value[0].text().startsWith("## ") }
            // 跳过h2 解析h3
            value.drop(1).groupByType(MarkdownElementTypes.ATX_3) {
                assertEquals(2, it.children.size, "h3 的node也是两部分")
                assertEquals("###", it.children.first().text(), "第一部分是 ###")
                // 第二部分就是 h3 的内容
                it.children.last().text().trim()
            }.filterKeys(String::isNotEmpty)
        }

        assertContentEquals(listOf("Added", "Changed"), h3versionMaps[unreleasedMark]!!.keys)
        assertContentEquals(listOf("Added", "Fixed"), h3versionMaps["1.0.0"]!!.keys)
    }
}
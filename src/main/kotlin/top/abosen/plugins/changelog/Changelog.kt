package top.abosen.plugins.changelog

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser
import top.abosen.plugins.changelog.exceptions.HeaderParseException
import top.abosen.plugins.changelog.exceptions.MissingFileException
import top.abosen.plugins.changelog.exceptions.MissingVersionException
import java.io.File

/**
 * @author  qiubaisen
 * @date  2021/6/7
 */

class Changelog(extension: ChangelogPluginExtension) {
    val content = File(extension.path).run {
        if (extension.path.isBlank() || !exists()) {
            throw MissingFileException(extension.path)
        }
        readText()
    }

    @Suppress("MaxLineLength")
    // [semver](https://semver.org/lang/zh-CN/)
    private val flavour = GFMFlavourDescriptor()
    private val parser = MarkdownParser(flavour)
    private val tree = parser.buildMarkdownTreeFromString(content)

    private val items = tree.children
        .groupByType(MarkdownElementTypes.ATX_2) {
            it.children.last().text().trim().run {
                // ## 后面的部分
                when (this) {
                    extension.unreleasedTerm -> this
                    else -> split("""[^-+.0-9a-zA-Z]+""".toRegex()).firstOrNull(
                        extension.headerParserRegex::matches
                    ) ?: throw HeaderParseException(this)
                }
            }
        }
        .filterKeys(String::isNotEmpty)
        .mapValues { (key, value) ->
            value
                .drop(1)    // 跳过 h2
                .groupByType(MarkdownElementTypes.ATX_3) {
                    it.children.last().text().trim()
                }
                .filterKeys(String::isNotEmpty)
                .mapValues {
                    it.value
                        .joinToString("") { node -> node.text() }
                        .split("""\n${Regex.escape(extension.itemPrefix)}""".toRegex())
                        .map { line -> extension.itemPrefix + line.trim('\n') }
                        .drop(1)    // 跳过 h3
                        .filterNot(String::isEmpty)
                }.run {
                    val isUnreleased = key == extension.unreleasedTerm
                    Item(Version.parse(value.first().text()), value.first(), this, isUnreleased)
                }
        }

    fun has(version: String) = items.containsKey(version)

    fun get(version: String) = items[version] ?: throw MissingVersionException(version)

    fun getLatest() = items[items.keys.first()] ?: throw MissingVersionException("any")

    fun getAll() = items

    inner class Item(
        val version: Version,
        private val header: ASTNode,
        private val items: Map<String, List<String>>,
        private val isUnreleased: Boolean = false
    ) {

        private var withHeader = false
        private var filterCallback: ((String) -> Boolean)? = null

        fun withHeader(header: Boolean) = apply {
            this.withHeader = header
        }

        fun withFilter(filter: ((String) -> Boolean)?) = apply {
            this.filterCallback = filter
        }

        fun getHeaderNode() = header

        fun getHeader() = header.text()

        fun getSections() = items
            .mapValues {
                it.value.filter { item -> filterCallback?.invoke(item) ?: true }
            }
            .filterNot {
                it.value.isEmpty() && !isUnreleased
            }

        fun toText() = getSections().entries
            .joinToString("\n\n") { (key, value) ->
                (listOfNotNull("### $key".takeIf { key.isNotEmpty() }) + value).joinToString("\n")
            }.trim().let {
                when {
                    withHeader -> "${getHeader()}\n$it"
                    else -> it
                }
            }

        fun toHTML(): String = TODO()

        fun toPlainText(): String = TODO()

        override fun toString() = toText()
    }

    private fun ASTNode.text() = getTextInNode(content).toString()

    /** 忽略目标[type]之前的内容，[values] 包含 [type]同级 及 下级内容*/
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
}

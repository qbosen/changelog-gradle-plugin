package top.abosen.plugins.changelog
import groovy.lang.Closure
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author  qiubaisen
 * @date  2021/6/8
 */


fun <T : Any> closure(function: () -> T) = object : Closure<T>(null) {
    @Suppress("unused")
    fun doCall() = function()
}

fun date(pattern: String = "yyyy-MM-dd") = SimpleDateFormat(pattern).format(Date())!!

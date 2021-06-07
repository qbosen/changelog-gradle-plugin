package top.abosen.plugins.changelog

import groovy.lang.Closure
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import top.abosen.plugins.changelog.exceptions.VersionNotSpecifiedException
import java.io.File
import java.util.regex.Pattern

/**
 * @author qiubaisen
 * @date 2021/6/7
 */
open class ChangelogPluginExtension(objects: ObjectFactory, private val projectDir: File) {

    /**
     * 语义化的版本 [semver](https://semver.org/)
     *
     *  cg1 = major, cg2 = minor, cg3 = patch, cg4 = prerelease and cg5 = buildmetadata
     */
    @Suppress("MaxLineLength")
    val semVerRegex =
        """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?|(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?${'$'}""".trimMargin()
            .toRegex() // ktlint-disable max-line-length

    @Optional
    @Internal
    private val groupsProperty: ListProperty<String> = objects.listProperty(String::class.java).apply {
        set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
    }

    /**
     *
     */
    var groups: List<String>
        get() = groupsProperty.getOrElse(emptyList())
        set(value) = groupsProperty.set(value)

    @Optional
    @Internal
    private val headerProperty = objects.property(Closure::class.java).apply {
        set(closure { "[$version]" })
    }
    var header: Closure<*>
        get() = headerProperty.get()
        set(value) = headerProperty.set(value)

    @Optional
    @Internal
    private val headerParserRegexProperty = objects.property(Regex::class.java).apply {
        set(semVerRegex)
    }
    var headerParserRegex: Regex
        get() = headerParserRegexProperty.get()
        set(value) = headerParserRegexProperty.set(headerParserRegexHelper(value))

    private fun <T> headerParserRegexHelper(t: T) = when (t) {
        is Regex -> t
        is String -> t.toRegex()
        is Pattern -> t.toRegex()
        else -> throw IllegalArgumentException("Unsupported type of $t. Expected value types: Regex, String, Pattern.")
    }

    @Optional
    @Internal
    private val pathProperty = objects.property(String::class.java).apply {
        set("$projectDir/CHANGELOG.md")
    }
    var path: String
        get() = pathProperty.get()
        set(value) = pathProperty.set(value)

    @Optional
    @Internal
    private val unreleasedTermProperty = objects.property(String::class.java).apply {
        set("[Unreleased]")
    }
    var unreleasedTerm: String
        get() = unreleasedTermProperty.get()
        set(value) = unreleasedTermProperty.set(value)

    @Optional
    @Internal
    private val itemPrefixProperty = objects.property(String::class.java).apply {
        set("-")
    }
    var itemPrefix: String
        get() = itemPrefixProperty.get()
        set(value) = itemPrefixProperty.set(value)

    @Internal
    val versionProperty: Property<String> = objects.property(String::class.java)
    var version: String
        get() = versionProperty.run {
            if (isPresent) {
                return get()
            }
            throw VersionNotSpecifiedException()
        }
        set(value) = versionProperty.set(value)


}

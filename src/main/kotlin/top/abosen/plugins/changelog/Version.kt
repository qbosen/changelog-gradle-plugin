package top.abosen.plugins.changelog

import top.abosen.plugins.changelog.exceptions.HeaderParseException

/**
 * @author  qiubaisen
 * @date  2021/6/8
 */
data class Version(val major: Int, val minor: Int, val patch: Int, val preRelease: String = "", val buildMeta: String = "") {
    val isPreRelease get() = preRelease.isNotEmpty()
    val hasBuildMeta get() = buildMeta.isNotEmpty()

    companion object {
        @Suppress("MaxLineLength")
                /** groups: 1:major 2:minor 3:patch 4:pre-release 5:metadata */
        val semVerRegex: Regex =
            """^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?$""".toRegex() // ktlint-disable max-line-length

        fun parse(header: String): Version = semVerRegex.find(header)?.run {
            Version(groupValues[1].toInt(), groupValues[2].toInt(), groupValues[3].toInt(), groupValues[4], groupValues[5])
        } ?: throw HeaderParseException(header)
    }
}
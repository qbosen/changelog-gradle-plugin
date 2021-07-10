package top.abosen.plugins.changelog.exceptions

import top.abosen.plugins.changelog.ChangelogPluginExtension

/**
 * @author  qiubaisen
 * @date  2021/6/7
 */

class MissingFileException(path: String) : Exception("Changelog file does not exist: $path")
class MissingVersionException(version: String) : Exception("Changelog version does not exist: $version")
class VersionNotSpecifiedException : Exception(
    "Version is missing. Please provide the project version to the `changelog.version` property explicitly."
)

class HeaderParseException(value: String) : Exception("Header '$value' does not contain version number. ")
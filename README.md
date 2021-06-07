# Changelog Gradle Plugin

## Configuration

Plugin can be configured with the following properties set in the `changelog {}` closure:

| Property                | Description                                                                | Default value                                                        |
| ----------------------- | -------------------------------------------------------------------------- | -------------------------------------------------------------------- |
| `version`               | Current project's version.                                   |                                                                      |
| `groups`                | List of groups created with a new Unreleased section.                      | `["Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"]` |
| `header`                | Closure that returns current header value.                                 | `{ "[$version]" }`                                                   |
| `headerParserRegex`     | `Regex`/`Pattern`/`String` used to extract version from the header string. | `null`, fallbacks to [`Changelog#semVerRegex`][semver-regex]         |
| `itemPrefix`            | Single item's prefix, allows to customise the bullet sign.                 | `"-"`                                                                |
| `keepUnreleasedSection` | Add Unreleased empty section after patching.                               | `true`                                                               |
| `patchEmpty`            | Patches changelog even if no release note is provided.                     | `true`                                                               |
| `path`                  | Path to the changelog file.                                                | `"${project.projectDir}/CHANGELOG.md"`                               |
| `unreleasedTerm`        | Unreleased section text.                                                   | `"[Unreleased]"`                                                     |

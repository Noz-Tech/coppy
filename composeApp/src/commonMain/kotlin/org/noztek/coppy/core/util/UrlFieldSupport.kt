package org.noztek.coppy.core.util

private val explicitUrlPrefixRegex = Regex("^https?://", RegexOption.IGNORE_CASE)
private val hostnameRegex = Regex(
    pattern = "^(localhost|((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,})(:\\d{1,5})?(/\\S*)?$",
)
private val ipv4Regex = Regex(
    pattern = "^(\\d{1,3}\\.){3}\\d{1,3}(:\\d{1,5})?(/\\S*)?$",
)

fun normalizeBrowserUrl(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return ""
    if (explicitUrlPrefixRegex.containsMatchIn(trimmed)) return trimmed
    return if (looksLikeHostTarget(trimmed) || trimmed.startsWith("www.", ignoreCase = true)) {
        "https://$trimmed"
    } else {
        trimmed
    }
}

fun isValidBrowserUrl(raw: String): Boolean {
    val normalized = normalizeBrowserUrl(raw)
    if (!explicitUrlPrefixRegex.containsMatchIn(normalized)) return false
    val withoutScheme = normalized.removePrefix("https://").removePrefix("http://")
    return withoutScheme.isNotBlank() &&
        !withoutScheme.contains(' ') &&
        (hostnameRegex.matches(withoutScheme) || ipv4Regex.matches(withoutScheme))
}

private fun looksLikeHostTarget(value: String): Boolean {
    return hostnameRegex.matches(value) || ipv4Regex.matches(value)
}

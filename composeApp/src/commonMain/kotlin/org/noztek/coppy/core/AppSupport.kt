package org.noztek.coppy.core

object AppSupport {
    const val feedbackEmail = "noztek@protonmail.com"
    const val playStoreUrl = "https://play.google.com/store/apps/details?id=org.noztek.coppy"

    fun feedbackMailTo(subject: String): String {
        return "mailto:$feedbackEmail?subject=${subject.encodeForUrl()}"
    }
}

private fun String.encodeForUrl(): String {
    return buildString(length) {
        for (char in this@encodeForUrl) {
            when (char) {
                ' ' -> append("%20")
                else -> append(char)
            }
        }
    }
}

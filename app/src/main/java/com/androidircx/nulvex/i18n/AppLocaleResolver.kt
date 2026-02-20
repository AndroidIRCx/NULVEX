package com.androidircx.nulvex.i18n

private val supportedBaseLanguages = setOf("en", "sr")

fun sanitizeLanguageTag(requestedTag: String): String {
    val base = requestedTag.substringBefore('-').lowercase()
    return if (base in supportedBaseLanguages) base else "en"
}

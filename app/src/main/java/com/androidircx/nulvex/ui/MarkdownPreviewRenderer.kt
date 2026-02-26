package com.androidircx.nulvex.ui

object MarkdownPreviewRenderer {
    private val orderedPattern = Regex("^\\s*(\\d+)\\.\\s+(.*)$")
    private val unorderedPattern = Regex("^\\s*[-*+]\\s+(.*)$")
    private val checklistPattern = Regex("^\\s*[-*+]\\s+\\[(x|X| )\\]\\s+(.*)$")
    private val headingPattern = Regex("^\\s{0,3}(#{1,6})\\s+(.*)$")
    private val linkPattern = Regex("\\[([^\\]]+)]\\(([^)]+)\\)")
    private val boldPattern = Regex("(\\*\\*|__)(.+?)\\1")
    private val italicPattern = Regex("(\\*|_)([^*_].*?)\\1")

    fun render(markdown: String): String {
        val output = mutableListOf<String>()
        var inCodeFence = false
        markdown.lines().forEach { line ->
            val trimmed = line.trimEnd()
            if (trimmed.startsWith("```")) {
                inCodeFence = !inCodeFence
                output += "```"
                return@forEach
            }
            if (inCodeFence) {
                output += trimmed
                return@forEach
            }

            val headingMatch = headingPattern.find(trimmed)
            if (headingMatch != null) {
                output += headingMatch.groupValues[2].uppercase()
                return@forEach
            }

            val checklistMatch = checklistPattern.find(trimmed)
            if (checklistMatch != null) {
                val checked = checklistMatch.groupValues[1].equals("x", ignoreCase = true)
                val marker = if (checked) "☑" else "☐"
                output += "$marker ${inline(checklistMatch.groupValues[2])}"
                return@forEach
            }

            val orderedMatch = orderedPattern.find(trimmed)
            if (orderedMatch != null) {
                output += "${orderedMatch.groupValues[1]}. ${inline(orderedMatch.groupValues[2])}"
                return@forEach
            }

            val unorderedMatch = unorderedPattern.find(trimmed)
            if (unorderedMatch != null) {
                output += "• ${inline(unorderedMatch.groupValues[1])}"
                return@forEach
            }

            output += inline(trimmed)
        }
        return output.joinToString("\n")
    }

    private fun inline(value: String): String {
        var result = value
        result = linkPattern.replace(result) { match ->
            val text = match.groupValues[1].trim()
            val url = match.groupValues[2].trim()
            if (text.isBlank()) url else "$text ($url)"
        }
        result = boldPattern.replace(result) { it.groupValues[2] }
        result = italicPattern.replace(result) { it.groupValues[2] }
        return result
    }
}


package com.androidircx.nulvex.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownPreviewRendererTest {

    @Test
    fun `renders headings and inline emphasis`() {
        val input = "# title\nHello **bold** and *italic*."

        val rendered = MarkdownPreviewRenderer.render(input)

        assertEquals("TITLE\nHello bold and italic.", rendered)
    }

    @Test
    fun `renders unordered ordered and checklist items`() {
        val input = """
            - one
            1. first
            - [x] done
            - [ ] todo
        """.trimIndent()

        val rendered = MarkdownPreviewRenderer.render(input)

        assertEquals("• one\n1. first\n☑ done\n☐ todo", rendered)
    }

    @Test
    fun `renders code fences and links as local text`() {
        val input = """
            Use [docs](https://example.com).
            ```kotlin
            val x = 1
            ```
        """.trimIndent()

        val rendered = MarkdownPreviewRenderer.render(input)

        assertEquals("Use docs (https://example.com).\n```\nval x = 1\n```", rendered)
    }

    @Test
    fun `xss style payloads stay inert plain text`() {
        val input = "Click [run](javascript:alert(1)) <script>alert('x')</script>"

        val rendered = MarkdownPreviewRenderer.render(input)

        assertTrue(rendered.contains("run (javascript:alert(1))"))
        assertTrue(rendered.contains("<script>alert('x')</script>"))
    }
}

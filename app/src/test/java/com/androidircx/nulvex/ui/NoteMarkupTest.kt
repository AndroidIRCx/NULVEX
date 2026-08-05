package com.androidircx.nulvex.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteMarkupTest {

    @Test
    fun containsHtmlDetectsTags() {
        assertTrue(NoteMarkup.containsHtml("hello <b>world</b>"))
        assertTrue(NoteMarkup.containsHtml("<table><tr><td>x</td></tr></table>"))
        assertFalse(NoteMarkup.containsHtml("just **markdown** and [link](x)"))
        assertFalse(NoteMarkup.containsHtml("a < b and c > d"))
    }

    private fun html(text: String): String =
        NoteMarkup.toHtmlDocument(text, "#000000", "#ffffff", "#ff0000", "#111111")

    @Test
    fun markdownConvertsToHtml() {
        val out = html("# Heading\n\n**bold** and *italic* and `code`")
        assertTrue(out.contains("<h1>"))
        assertTrue(out.contains("<strong>bold</strong>"))
        assertTrue(out.contains("<em>italic</em>"))
        assertTrue(out.contains("<code>code</code>"))
    }

    @Test
    fun bbcodeConvertsToHtml() {
        val out = html("[b]strong[/b] [i]em[/i] [url=https://x.com]link[/url]")
        assertTrue(out.contains("<strong>strong</strong>"))
        assertTrue(out.contains("<em>em</em>"))
        assertTrue(out.contains("href=\"https://x.com\""))
    }

    @Test
    fun rawHtmlIsPreservedButScriptsStripped() {
        val out = html("<p>keep me</p><script>alert('x')</script>")
        assertTrue(out.contains("keep me"))
        assertFalse(out.contains("<script"))
        assertFalse(out.contains("alert("))
    }

    @Test
    fun eventHandlersAndJavascriptUrlsAreStripped() {
        val out = html("<a href=\"javascript:evil()\" onclick=\"evil()\">x</a>")
        assertFalse(out.lowercase().contains("javascript:"))
        assertFalse(out.lowercase().contains("onclick"))
    }
}

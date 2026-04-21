package com.androidircx.nulvex.i18n

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLocaleResolverTest {

    @Test
    fun `sanitizeLanguageTag keeps supported english`() {
        assertEquals("en", sanitizeLanguageTag("en"))
    }

    @Test
    fun `sanitizeLanguageTag normalizes supported serbian variant to base locale`() {
        assertEquals("sr", sanitizeLanguageTag("sr-Latn-RS"))
    }

    @Test
    fun `sanitizeLanguageTag falls back to english for unsupported locale`() {
        assertEquals("en", sanitizeLanguageTag("de-DE"))
    }
}


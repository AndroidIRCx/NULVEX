package com.androidircx.nulvex.i18n

import android.content.Context
import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.MessageDigest

class TxStringsTest {

    @Test
    fun `context tx returns translated resource when hash key exists`() {
        val text = "Security Timeline"
        val expectedKey = toTxKey(text)
        val resources = mockk<Resources>()
        val context = mockk<Context>()

        every { context.resources } returns resources
        every { context.packageName } returns "com.androidircx.nulvex"
        every { resources.getIdentifier(expectedKey, "string", "com.androidircx.nulvex") } returns 42
        every { resources.getString(42) } returns "Bezbednosna vremenska linija"

        val result = context.tx(text)

        assertEquals("Bezbednosna vremenska linija", result)
        verify(exactly = 1) { resources.getIdentifier(expectedKey, "string", "com.androidircx.nulvex") }
        verify(exactly = 1) { resources.getString(42) }
    }

    @Test
    fun `context tx falls back to source text when translation key is missing`() {
        val text = "Unknown sentence"
        val expectedKey = toTxKey(text)
        val resources = mockk<Resources>()
        val context = mockk<Context>()

        every { context.resources } returns resources
        every { context.packageName } returns "com.androidircx.nulvex"
        every { resources.getIdentifier(expectedKey, "string", "com.androidircx.nulvex") } returns 0

        val result = context.tx(text)

        assertEquals(text, result)
        verify(exactly = 1) { resources.getIdentifier(expectedKey, "string", "com.androidircx.nulvex") }
        verify(exactly = 0) { resources.getString(any<Int>()) }
    }

    private fun toTxKey(text: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
            .digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
            .take(10)
        return "tx_auto_$digest"
    }
}


package com.androidircx.nulvex.security

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class WipeExtensionsTest {

    @Test
    fun `byte array wipe overwrites all bytes with zero`() {
        val input = byteArrayOf(1, 2, 3, 4, 5)

        input.wipe()

        assertArrayEquals(byteArrayOf(0, 0, 0, 0, 0), input)
    }

    @Test
    fun `char array wipe overwrites all chars with null char`() {
        val input = charArrayOf('n', 'u', 'l', 'v', 'e', 'x')

        input.wipe()

        assertArrayEquals(charArrayOf('\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000'), input)
    }
}


package com.androidircx.nulvex.data

import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class VaultSessionTest {

    @Test
    fun `close wipes key material and closes database`() {
        val database = mockk<NulvexDatabase>(relaxed = true)
        val noteKey = byteArrayOf(7, 8, 9, 10)
        val session = VaultSession(database = database, noteKey = noteKey)

        session.close()

        assertArrayEquals(byteArrayOf(0, 0, 0, 0), noteKey)
        verify(exactly = 1) { database.close() }
    }
}


package com.androidircx.nulvex.data

import android.content.Context
import com.androidircx.nulvex.security.VaultProfile
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for VaultSessionManager state machine.
 *
 * Tests cover null-state behavior only. The open() flow requires a real
 * SQLCipher database and is covered by instrumented integration tests.
 */
class VaultSessionManagerTest {

    private lateinit var mockContext: Context
    private lateinit var sessionManager: VaultSessionManager

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        sessionManager = VaultSessionManager(mockContext)
    }

    @Test
    fun `getActive returns null before any session is opened`() {
        assertNull(sessionManager.getActive())
    }

    @Test
    fun `getActiveProfile returns null before any session is opened`() {
        assertNull(sessionManager.getActiveProfile())
    }

    @Test
    fun `close on null session does not throw`() {
        sessionManager.close()

        assertNull(sessionManager.getActive())
        assertNull(sessionManager.getActiveProfile())
    }

    @Test
    fun `close multiple times does not throw`() {
        sessionManager.close()
        sessionManager.close()
        sessionManager.close()

        assertNull(sessionManager.getActive())
    }

    @Test
    fun `getActive and getActiveProfile are thread-safe null reads`() {
        val results = mutableListOf<Boolean>()
        val threads = (1..10).map {
            Thread {
                val active = sessionManager.getActive()
                val profile = sessionManager.getActiveProfile()
                synchronized(results) {
                    results.add(active == null && profile == null)
                }
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        assertTrue(results.all { it })
        assertEquals(10, results.size)
    }
}

package com.androidircx.nulvex.security

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityEventStoreTest {

    private fun makeStore(stored: String? = null): Pair<SecurityEventStore, SharedPreferences.Editor> {
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { editor.putString(any(), any()) } returns editor

        val prefs = mockk<SharedPreferences>()
        every { prefs.getString("events", null) } returns stored
        every { prefs.edit() } returns editor

        val context = mockk<Context>(relaxed = true)
        every { context.applicationContext } returns context
        // Fall back to plain prefs since MasterKey won't work in JVM tests
        every { context.getSharedPreferences("nulvex_security_events", Context.MODE_PRIVATE) } returns prefs

        return SecurityEventStore(context) to editor
    }

    @Test
    fun record_storesEventAsJson() {
        val (store, editor) = makeStore()
        store.record(SecurityEventStore.EVENT_UNLOCK_SUCCESS, "real")
        val slot = slot<String>()
        verify { editor.putString("events", capture(slot)) }
        val json = slot.captured
        assertTrue(json.contains(SecurityEventStore.EVENT_UNLOCK_SUCCESS))
        assertTrue(json.contains("real"))
    }

    @Test
    fun listEvents_returnsEmptyWhenNoData() {
        val (store, _) = makeStore(stored = null)
        val events = store.listEvents()
        assertTrue(events.isEmpty())
    }

    @Test
    fun listEvents_parsesStoredEvents() {
        val stored = """[{"id":"abc","type":"unlock_success","detail":"real","ts":12345}]"""
        val (store, _) = makeStore(stored = stored)
        val events = store.listEvents()
        assertEquals(1, events.size)
        assertEquals(SecurityEventStore.EVENT_UNLOCK_SUCCESS, events[0].type)
        assertEquals("real", events[0].detail)
        assertEquals(12345L, events[0].timestampMillis)
    }

    @Test
    fun record_newestEventFirst() {
        val existing = """[{"id":"old","type":"unlock_fail","detail":"","ts":1000}]"""
        val (store, editor) = makeStore(stored = existing)
        store.record(SecurityEventStore.EVENT_PANIC_WIPE)
        val slot = slot<String>()
        verify { editor.putString("events", capture(slot)) }
        val json = slot.captured
        // panic_wipe should appear before unlock_fail in the JSON array
        val panicIdx = json.indexOf(SecurityEventStore.EVENT_PANIC_WIPE)
        val failIdx = json.indexOf(SecurityEventStore.EVENT_UNLOCK_FAIL)
        assertTrue(panicIdx < failIdx)
    }

    @Test
    fun record_invalidJsonDoesNotCrash() {
        val (store, editor) = makeStore(stored = "not-json")
        store.record(SecurityEventStore.EVENT_LOCKOUT, "30s")
        val slot = slot<String>()
        verify { editor.putString("events", capture(slot)) }
        assertTrue(slot.captured.contains(SecurityEventStore.EVENT_LOCKOUT))
    }

    @Test
    fun eventTypeConstants_areNonBlank() {
        listOf(
            SecurityEventStore.EVENT_UNLOCK_SUCCESS,
            SecurityEventStore.EVENT_UNLOCK_FAIL,
            SecurityEventStore.EVENT_LOCKOUT,
            SecurityEventStore.EVENT_PANIC_WIPE,
            SecurityEventStore.EVENT_KEY_IMPORT,
            SecurityEventStore.EVENT_KEY_DELETE,
            SecurityEventStore.EVENT_KEY_GENERATE,
            SecurityEventStore.EVENT_SYNC_AUTH_CHANGE,
            SecurityEventStore.EVENT_KEY_ROTATION,
            SecurityEventStore.EVENT_BACKUP_EXPORT,
            SecurityEventStore.EVENT_BACKUP_IMPORT
        ).forEach { type -> assertTrue(type.isNotBlank()) }
    }
}

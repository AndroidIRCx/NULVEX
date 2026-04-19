package com.androidircx.nulvex.sync

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncPreferencesObservabilityTest {

    /** Build a SyncPreferences backed by a mockk SharedPreferences. */
    private fun buildPrefs(
        lastSyncAt: Long = 0L,
        lastSyncConflicts: Int = 0
    ): Pair<SyncPreferences, SharedPreferences.Editor> {
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { editor.putLong(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putString(any(), any()) } returns editor

        val prefs = mockk<SharedPreferences>()
        // General catch-alls first (lowest priority in MockK — later stubs shadow earlier ones)
        every { prefs.getString(any(), null) } returns null
        every { prefs.getLong(any(), any()) } returns 0L
        every { prefs.getInt(any(), any()) } returns 0
        every { prefs.edit() } returns editor
        every { prefs.all } returns emptyMap<String, Any>()
        // Specific overrides last (highest priority)
        every { prefs.getLong("last_sync_at", 0L) } returns lastSyncAt
        every { prefs.getInt("last_sync_conflicts", 0) } returns lastSyncConflicts

        val context = mockk<Context>(relaxed = true)
        every { context.applicationContext } returns context
        // Both LEGACY and SECURE prefs names map to same mock
        every { context.getSharedPreferences(any(), any()) } returns prefs

        return SyncPreferences(context) to editor
    }

    @Test
    fun getLastSyncAt_returnsZeroByDefault() {
        val (prefs, _) = buildPrefs(lastSyncAt = 0L)
        assertEquals(0L, prefs.getLastSyncAt())
    }

    @Test
    fun getLastSyncAt_returnsStoredTimestamp() {
        val ts = 1_700_000_000_000L
        val (prefs, _) = buildPrefs(lastSyncAt = ts)
        assertEquals(ts, prefs.getLastSyncAt())
    }

    @Test
    fun getLastSyncConflictCount_returnsZeroByDefault() {
        val (prefs, _) = buildPrefs(lastSyncConflicts = 0)
        assertEquals(0, prefs.getLastSyncConflictCount())
    }

    @Test
    fun getLastSyncConflictCount_returnsStoredCount() {
        val (prefs, _) = buildPrefs(lastSyncConflicts = 3)
        assertEquals(3, prefs.getLastSyncConflictCount())
    }

    @Test
    fun setLastSyncResult_writesTimestampAndConflictCount() {
        val (syncPrefs, editor) = buildPrefs()
        syncPrefs.setLastSyncResult(timestampMillis = 999L, conflictCount = 2)

        val tsSlot = slot<Long>()
        val countSlot = slot<Int>()
        verify { editor.putLong("last_sync_at", capture(tsSlot)) }
        verify { editor.putInt("last_sync_conflicts", capture(countSlot)) }
        assertEquals(999L, tsSlot.captured)
        assertEquals(2, countSlot.captured)
    }

    @Test
    fun setLastSyncResult_zeroConflicts_isWritten() {
        val (syncPrefs, editor) = buildPrefs()
        syncPrefs.setLastSyncResult(timestampMillis = 42L, conflictCount = 0)
        verify { editor.putInt("last_sync_conflicts", 0) }
        verify { editor.putLong("last_sync_at", 42L) }
    }
}

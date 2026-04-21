package com.androidircx.nulvex.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncPreferencesTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val legacyPrefs = context.getSharedPreferences("nulvex_sync_prefs", Context.MODE_PRIVATE)
    private val securePrefs = context.getSharedPreferences("nulvex_sync_secure_prefs", Context.MODE_PRIVATE)
    private val securePrefsV2 = context.getSharedPreferences("nulvex_sync_secure_prefs_v2", Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        legacyPrefs.edit().clear().commit()
        securePrefs.edit().clear().commit()
        securePrefsV2.edit().clear().commit()
    }

    @After
    fun tearDown() {
        legacyPrefs.edit().clear().commit()
        securePrefs.edit().clear().commit()
        securePrefsV2.edit().clear().commit()
    }

    @Test
    fun setAuthToken_roundTripsFromSecureStore() {
        val prefs = SyncPreferences(context)
        val token = SyncAuthToken(
            accessToken = "access-1",
            refreshToken = "refresh-1",
            expiresAtEpochMillis = 12345L,
            deviceId = "device-1"
        )

        prefs.setAuthToken("real", token)
        val loaded = prefs.getAuthToken("real")

        assertNotNull(loaded)
        assertEquals("access-1", loaded?.accessToken)
        assertEquals("refresh-1", loaded?.refreshToken)
        assertEquals(12345L, loaded?.expiresAtEpochMillis)
        assertEquals("device-1", loaded?.deviceId)
    }

    @Test
    fun migratesLegacyPrefsToSecureStore() {
        legacyPrefs.edit()
            .putString("device_id", "legacy-device")
            .putString("access_token_real", "legacy-access")
            .putString("refresh_token_real", "legacy-refresh")
            .putLong("expires_at_real", 999L)
            .putString("token_device_id_real", "legacy-device")
            .commit()

        val prefs = SyncPreferences(context)
        val loaded = prefs.getAuthToken("real")

        assertNotNull(loaded)
        assertEquals("legacy-access", loaded?.accessToken)
        assertEquals("legacy-refresh", loaded?.refreshToken)
        assertEquals(999L, loaded?.expiresAtEpochMillis)
        assertEquals("legacy-device", loaded?.deviceId)
        assertTrue(legacyPrefs.all.isEmpty())
    }
}

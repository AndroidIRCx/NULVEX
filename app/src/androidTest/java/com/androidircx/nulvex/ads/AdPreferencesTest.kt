package com.androidircx.nulvex.ads

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdPreferencesTest {

    private lateinit var context: Context
    private lateinit var prefs: AdPreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("nulvex_ad_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        prefs = AdPreferences(context)
    }

    @Test
    fun removeAdsLifetime_makesIsAdFreeTrue() {
        assertFalse(prefs.isAdFree())

        prefs.enableRemoveAdsLifetime()

        assertTrue(prefs.hasRemoveAdsLifetime())
        assertTrue(prefs.isAdFree())
    }

    @Test
    fun proFeaturesLifetime_enablesUnlimitedShares() {
        prefs.enableProFeaturesLifetime()

        assertTrue(prefs.hasProFeaturesLifetime())
        assertTrue(prefs.hasUnlimitedShares())
    }

    @Test
    fun consumeShareCredits_withPro_doesNotDecreaseBalance() {
        prefs.addShareCredits(2)
        prefs.enableProFeaturesLifetime()

        val consumed = prefs.consumeShareCredits(10)

        assertTrue(consumed)
        assertEquals(2, prefs.getShareCredits())
    }

    @Test
    fun consumeShareCredits_withoutPro_obeysBalance() {
        prefs.addShareCredits(2)

        assertTrue(prefs.consumeShareCredits(1))
        assertEquals(1, prefs.getShareCredits())

        assertFalse(prefs.consumeShareCredits(2))
        assertEquals(1, prefs.getShareCredits())
    }
}

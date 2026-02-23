package com.androidircx.nulvex.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppPreferencesTest {

    private lateinit var prefs: AppPreferences

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)
            .edit().clear().commit()
        prefs = AppPreferences(context)
    }

    @Test
    fun isPinScrambleEnabled_defaultFalse() {
        assertFalse(prefs.isPinScrambleEnabled())
    }

    @Test
    fun setPinScrambleEnabled_true_persistsValue() {
        prefs.setPinScrambleEnabled(true)
        assertTrue(prefs.isPinScrambleEnabled())
    }

    @Test
    fun setPinScrambleEnabled_toggleBackToFalse() {
        prefs.setPinScrambleEnabled(true)
        prefs.setPinScrambleEnabled(false)
        assertFalse(prefs.isPinScrambleEnabled())
    }

    @Test
    fun isHidePinLengthEnabled_defaultFalse() {
        assertFalse(prefs.isHidePinLengthEnabled())
    }

    @Test
    fun setHidePinLengthEnabled_true_persistsValue() {
        prefs.setHidePinLengthEnabled(true)
        assertTrue(prefs.isHidePinLengthEnabled())
    }

    @Test
    fun setHidePinLengthEnabled_toggleBackToFalse() {
        prefs.setHidePinLengthEnabled(true)
        prefs.setHidePinLengthEnabled(false)
        assertFalse(prefs.isHidePinLengthEnabled())
    }

    @Test
    fun pinScrambleAndHidePinLength_areIndependent() {
        prefs.setPinScrambleEnabled(true)
        prefs.setHidePinLengthEnabled(false)

        assertTrue(prefs.isPinScrambleEnabled())
        assertFalse(prefs.isHidePinLengthEnabled())
    }
}

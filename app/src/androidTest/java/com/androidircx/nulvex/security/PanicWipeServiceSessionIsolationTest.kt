package com.androidircx.nulvex.security

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PanicWipeServiceSessionIsolationTest {

    @Before
    fun setUp() = runBlocking {
        val app: Application = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        VaultServiceLocator.panicWipeService().wipeAll()

        val authController = VaultServiceLocator.vaultAuthController()
        authController.setupRealPin("2468".toCharArray())
        authController.setupDecoyPin("1357".toCharArray())
        val profile = authController.unlockWithPin("2468".toCharArray())
        assertEquals(VaultProfile.REAL, profile)
    }

    @Test
    fun wipeDecoyOnlyKeepsRealSessionUsable() = runBlocking {
        val panicWipeService = VaultServiceLocator.panicWipeService()
        val vaultService = VaultServiceLocator.vaultService()

        panicWipeService.wipeDecoyOnly()

        val notes = vaultService.listNotes()
        assertNotNull(notes)
    }

    @Test
    fun realAndDecoyPinsRemainUnlockableWithDecoyBiometricTargetFlag() = runBlocking {
        val appPreferences = VaultServiceLocator.appPreferences()
        val vaultService = VaultServiceLocator.vaultService()
        val authController = VaultServiceLocator.vaultAuthController()

        appPreferences.setDecoyBiometricEnabled(true)
        appPreferences.setBiometricTargetVault("decoy")

        vaultService.lock()
        val realProfile = authController.unlockWithPin("2468".toCharArray())
        assertEquals(VaultProfile.REAL, realProfile)

        vaultService.lock()
        val decoyProfile = authController.unlockWithPin("1357".toCharArray())
        assertEquals(VaultProfile.DECOY, decoyProfile)
    }
}


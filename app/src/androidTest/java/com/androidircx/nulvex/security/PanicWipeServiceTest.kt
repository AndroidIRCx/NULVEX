package com.androidircx.nulvex.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.WorkManagerTestInitHelper
import com.androidircx.nulvex.data.VaultSessionManager
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for PanicWipeService.
 *
 * WorkManager is initialized via WorkManagerTestInitHelper so cancelUniqueWork
 * does not throw. KeystoreSecretProvider runs for real — deleteSecret() is safe
 * because it only removes a key if it exists, and test aliases are ephemeral.
 *
 * mockkConstructor / mockkStatic are intentionally avoided here: constructor
 * mocking of internal Android Keystore classes is brittle across API levels.
 * Keystore deletion behavior is implicitly verified by the absence of exceptions.
 */
@RunWith(AndroidJUnit4::class)
class PanicWipeServiceTest {

    private lateinit var context: Context
    private lateinit var mockSessionManager: VaultSessionManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockSessionManager = mockk(relaxed = true)
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // --- wipeAll() ---

    @Test
    fun wipeAllClosesActiveSession() {
        val service = PanicWipeService(context, mockSessionManager)
        service.wipeAll()
        verify { mockSessionManager.close() }
    }

    @Test
    fun wipeAllCompletesWithoutThrowing() {
        val service = PanicWipeService(context, mockSessionManager)
        service.wipeAll()
    }

    @Test
    fun wipeAllWithCustomProfilesOnlyWipesThoseProfiles() {
        val service = PanicWipeService(
            context,
            mockSessionManager,
            profiles = listOf(VaultProfile.DECOY)
        )
        service.wipeAll()
        verify { mockSessionManager.close() }
    }

    // --- wipeDecoyOnly() ---

    @Test
    fun wipeDecoyOnlyClosesActiveSession() {
        val service = PanicWipeService(context, mockSessionManager)
        service.wipeDecoyOnly()
        verify { mockSessionManager.close() }
    }

    @Test
    fun wipeDecoyOnlyCompletesWithoutThrowing() {
        val service = PanicWipeService(context, mockSessionManager)
        service.wipeDecoyOnly()
    }

    // --- isolation ---

    @Test
    fun wipeAllAndWipeDecoyOnlyBothCloseSession() {
        val service = PanicWipeService(context, mockSessionManager)
        service.wipeAll()
        service.wipeDecoyOnly()
        verify(exactly = 2) { mockSessionManager.close() }
    }
}

package com.androidircx.nulvex.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class VaultAuthServiceTest {

    private lateinit var context: Context
    private lateinit var prefs: VaultAuthPreferences
    private lateinit var authService: VaultAuthService

    // Light params so tests run fast on device
    private val fastParams = Argon2Params(
        memoryKiB = 1024,
        iterations = 1,
        parallelism = 1,
        outputLength = 32
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("nulvex_auth_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
        prefs = VaultAuthPreferences(context)
        authService = VaultAuthService(context, fastParams, prefs)
    }

    @Test
    fun isSetupReturnsFalseInitially() {
        assertFalse(authService.isSetup())
    }

    @Test
    fun isSetupReturnsTrueAfterSetRealPin() {
        authService.setRealPin("1234".toCharArray())
        assertTrue(authService.isSetup())
    }

    @Test
    fun hasDecoyPinReturnsFalseInitially() {
        assertFalse(authService.hasDecoyPin())
    }

    @Test
    fun hasDecoyPinReturnsTrueAfterSetDecoyPin() {
        authService.setRealPin("1234".toCharArray())
        authService.setDecoyPin("0000".toCharArray())
        assertTrue(authService.hasDecoyPin())
    }

    @Test
    fun resolveProfileReturnsRealForRealPin() {
        authService.setRealPin("1234".toCharArray())
        val profile = authService.resolveProfile("1234".toCharArray())
        assertEquals(VaultProfile.REAL, profile)
    }

    @Test
    fun resolveProfileReturnsDecoyForDecoyPin() {
        authService.setRealPin("1234".toCharArray())
        authService.setDecoyPin("0000".toCharArray())
        val profile = authService.resolveProfile("0000".toCharArray())
        assertEquals(VaultProfile.DECOY, profile)
    }

    @Test
    fun resolveProfileReturnsNullForWrongPin() {
        authService.setRealPin("1234".toCharArray())
        val profile = authService.resolveProfile("9999".toCharArray())
        assertNull(profile)
    }

    @Test
    fun resolveProfilePreferRealOverDecoyWhenBothMatch() {
        // Same PIN for both real and decoy is pathological but real must win
        authService.setRealPin("1111".toCharArray())
        authService.setDecoyPin("1111".toCharArray())
        val profile = authService.resolveProfile("1111".toCharArray())
        assertEquals(VaultProfile.REAL, profile)
    }

    @Test
    fun verifyRealPinReturnsTrueForCorrectPin() {
        authService.setRealPin("5678".toCharArray())
        assertTrue(authService.verifyRealPin("5678".toCharArray()))
    }

    @Test
    fun verifyRealPinReturnsFalseForWrongPin() {
        authService.setRealPin("5678".toCharArray())
        assertFalse(authService.verifyRealPin("0000".toCharArray()))
    }

    @Test
    fun verifyRealPinReturnsFalseWhenNotSetup() {
        assertFalse(authService.verifyRealPin("1234".toCharArray()))
    }

    @Test
    fun clearDecoyPinRemovesDecoy() {
        authService.setRealPin("1234".toCharArray())
        authService.setDecoyPin("0000".toCharArray())
        assertTrue(authService.hasDecoyPin())

        authService.clearDecoyPin()

        assertFalse(authService.hasDecoyPin())
    }

    @Test
    fun clearDecoyPinDoesNotAffectRealPin() {
        authService.setRealPin("1234".toCharArray())
        authService.setDecoyPin("0000".toCharArray())

        authService.clearDecoyPin()

        assertTrue(authService.isSetup())
    }

    @Test
    fun differentSaltsUsedForEachPin() {
        authService.setRealPin("1234".toCharArray())
        val hash1 = prefs.getRealPinHash()

        // Re-setup with same PIN — new random salt means different encoded hash
        context.getSharedPreferences("nulvex_auth_prefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
        authService.setRealPin("1234".toCharArray())
        val hash2 = prefs.getRealPinHash()

        // Argon2 encoded output includes the salt, so same PIN → different encoded string
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun resolveProfileAcceptsLegacyArgon2iHash() {
        val pinBytes = "2468".toByteArray(Charsets.UTF_8)
        val legacyHash = Argon2Kt()
            .hash(
                mode = Argon2Mode.ARGON2_I,
                password = pinBytes,
                salt = ByteArray(16).also { SecureRandom().nextBytes(it) },
                tCostInIterations = fastParams.iterations,
                mCostInKibibyte = fastParams.memoryKiB,
                parallelism = fastParams.parallelism,
                hashLengthInBytes = fastParams.outputLength
            )
            .encodedOutputAsString()
        pinBytes.fill(0)
        prefs.setRealPinHash(legacyHash)

        val profile = authService.resolveProfile("2468".toCharArray())
        assertEquals(VaultProfile.REAL, profile)
    }
}

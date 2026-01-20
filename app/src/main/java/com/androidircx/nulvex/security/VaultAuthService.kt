package com.androidircx.nulvex.security

import android.content.Context
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.androidircx.nulvex.security.wipe

class VaultAuthService(
    context: Context,
    private val params: Argon2Params = Argon2Params.DEFAULT,
    private val prefs: VaultAuthPreferences = VaultAuthPreferences(context)
) {
    private val argon2 = Argon2Kt()

    fun isSetup(): Boolean = prefs.getRealPinHash() != null

    fun setRealPin(pin: CharArray) {
        val hash = hashPin(pin)
        prefs.setRealPinHash(hash)
    }

    fun setDecoyPin(pin: CharArray) {
        val hash = hashPin(pin)
        prefs.setDecoyPinHash(hash)
    }

    fun clearDecoyPin() {
        prefs.clearDecoyPinHash()
    }

    fun resolveProfile(pin: CharArray): VaultProfile? {
        val realHash = prefs.getRealPinHash()
        val decoyHash = prefs.getDecoyPinHash()
        val pinBytes = pin.concatToString().toByteArray(Charsets.UTF_8)
        val matchReal = realHash != null && argon2.verify(Argon2Mode.ARGON2_ID, realHash, pinBytes)
        if (matchReal) {
            return VaultProfile.REAL
        }
        val matchDecoy = decoyHash != null && argon2.verify(Argon2Mode.ARGON2_ID, decoyHash, pinBytes)
        pinBytes.fill(0)
        return if (matchDecoy) VaultProfile.DECOY else null
    }

    private fun hashPin(pin: CharArray): String {
        val pinBytes = pin.concatToString().toByteArray(Charsets.UTF_8)
        val result = try {
            argon2.hash(
                mode = Argon2Mode.ARGON2_ID,
                password = pinBytes,
                salt = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) },
                tCostInIterations = params.iterations,
                mCostInKibibyte = params.memoryKiB,
                parallelism = params.parallelism,
                hashLengthInBytes = params.outputLength
            )
        } finally {
            pinBytes.fill(0)
            pin.wipe()
        }
        return result.encodedOutputAsString()
    }
}

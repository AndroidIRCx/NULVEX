package com.androidircx.nulvex.security

import com.androidircx.nulvex.data.VaultService

class VaultAuthController(
    private val authService: VaultAuthService,
    private val vaultService: VaultService
) {
    fun isSetup(): Boolean = authService.isSetup()
    fun isDecoyEnabled(): Boolean = authService.hasDecoyPin()

    fun setupRealPin(pin: CharArray) {
        authService.setRealPin(pin)
    }

    fun setupDecoyPin(pin: CharArray) {
        authService.setDecoyPin(pin)
    }

    fun clearDecoyPin() {
        authService.clearDecoyPin()
    }

    suspend fun unlockWithPin(pin: CharArray): VaultProfile? {
        val profile = authService.resolveProfile(pin)
        if (profile == null) {
            pin.wipe()
            return null
        }
        return try {
            vaultService.unlock(pin, profile)
            profile
        } catch (e: RuntimeException) {
            pin.wipe()
            throw e
        } catch (_: Exception) {
            pin.wipe()
            null
        }
    }

    suspend fun changeRealPin(oldPin: CharArray, newPin: CharArray): Boolean {
        if (!authService.verifyRealPin(oldPin)) {
            oldPin.wipe()
            newPin.wipe()
            return false
        }
        // Compute the new PIN hash from a copy up front: deriveMasterKey() inside the
        // vault rekey wipes the CharArray it receives, so passing newPin straight through
        // would leave us hashing all-zeros — the previous behaviour that permanently
        // locked users out after a PIN change. Store the hash only after the rekey
        // succeeds so a failed rekey leaves the old PIN working.
        val newHash = authService.computeRealPinHash(newPin.copyOf())
        return try {
            vaultService.changeRealPin(oldPin.copyOf(), newPin.copyOf())
            authService.storeRealPinHash(newHash)
            true
        } catch (_: Exception) {
            false
        } finally {
            oldPin.wipe()
            newPin.wipe()
        }
    }

    fun lock() {
        vaultService.lock()
    }
}

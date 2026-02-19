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
        } catch (_: Exception) {
            pin.wipe()
            null
        }
    }

    suspend fun changeRealPin(oldPin: CharArray, newPin: CharArray): Boolean {
        if (!authService.verifyRealPin(oldPin)) {
            oldPin.wipe()
            return false
        }
        return try {
            vaultService.changeRealPin(oldPin, newPin)
            authService.setRealPin(newPin)
            newPin.wipe()
            true
        } catch (_: Exception) {
            oldPin.wipe()
            newPin.wipe()
            false
        }
    }

    fun lock() {
        vaultService.lock()
    }
}

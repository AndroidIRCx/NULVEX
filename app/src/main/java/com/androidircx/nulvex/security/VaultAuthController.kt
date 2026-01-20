package com.androidircx.nulvex.security

import com.androidircx.nulvex.data.VaultService

class VaultAuthController(
    private val authService: VaultAuthService,
    private val vaultService: VaultService
) {
    fun isSetup(): Boolean = authService.isSetup()

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
        vaultService.unlock(pin, profile)
        return profile
    }

    fun lock() {
        vaultService.lock()
    }
}

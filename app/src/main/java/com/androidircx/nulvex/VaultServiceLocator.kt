package com.androidircx.nulvex

import android.content.Context
import com.androidircx.nulvex.data.VaultService
import com.androidircx.nulvex.data.VaultSessionManager
import com.androidircx.nulvex.security.VaultAuthController
import com.androidircx.nulvex.security.VaultAuthService
import com.androidircx.nulvex.security.PanicWipeService

object VaultServiceLocator {
    private lateinit var sessionManager: VaultSessionManager
    private lateinit var vaultService: VaultService
    private lateinit var panicWipeService: PanicWipeService
    private lateinit var vaultAuthService: VaultAuthService
    private lateinit var vaultAuthController: VaultAuthController

    fun init(context: Context) {
        val appContext = context.applicationContext
        sessionManager = VaultSessionManager(appContext)
        vaultService = VaultService(sessionManager)
        panicWipeService = PanicWipeService(appContext, sessionManager)
        vaultAuthService = VaultAuthService(appContext)
        vaultAuthController = VaultAuthController(vaultAuthService, vaultService)
    }

    fun sessionManager(): VaultSessionManager = sessionManager
    fun vaultService(): VaultService = vaultService
    fun panicWipeService(): PanicWipeService = panicWipeService
    fun vaultAuthService(): VaultAuthService = vaultAuthService
    fun vaultAuthController(): VaultAuthController = vaultAuthController
}

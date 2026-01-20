package com.androidircx.nulvex

import android.content.Context
import com.androidircx.nulvex.data.VaultService
import com.androidircx.nulvex.data.VaultSessionManager
import com.androidircx.nulvex.security.VaultAuthController
import com.androidircx.nulvex.security.VaultAuthService
import com.androidircx.nulvex.security.PanicWipeService
import com.androidircx.nulvex.security.AppPreferences

object VaultServiceLocator {
    private lateinit var sessionManager: VaultSessionManager
    private lateinit var vaultService: VaultService
    private lateinit var panicWipeService: PanicWipeService
    private lateinit var vaultAuthService: VaultAuthService
    private lateinit var vaultAuthController: VaultAuthController
    private lateinit var appPreferences: AppPreferences

    fun init(context: Context) {
        val appContext = context.applicationContext
        sessionManager = VaultSessionManager(appContext)
        vaultService = VaultService(sessionManager)
        panicWipeService = PanicWipeService(appContext, sessionManager)
        vaultAuthService = VaultAuthService(appContext)
        vaultAuthController = VaultAuthController(vaultAuthService, vaultService)
        appPreferences = AppPreferences(appContext)
    }

    fun sessionManager(): VaultSessionManager = sessionManager
    fun vaultService(): VaultService = vaultService
    fun panicWipeService(): PanicWipeService = panicWipeService
    fun vaultAuthService(): VaultAuthService = vaultAuthService
    fun vaultAuthController(): VaultAuthController = vaultAuthController
    fun appPreferences(): AppPreferences = appPreferences
}

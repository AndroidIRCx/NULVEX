package com.androidircx.nulvex

import android.content.Context
import com.androidircx.nulvex.ads.AdManager
import com.androidircx.nulvex.ads.AdPreferences
import com.androidircx.nulvex.data.VaultService
import com.androidircx.nulvex.data.VaultSessionManager
import com.androidircx.nulvex.pro.EncryptedBackupService
import com.androidircx.nulvex.pro.BackupRegistryStore
import com.androidircx.nulvex.pro.SharedKeyStore
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
    private lateinit var adPreferences: AdPreferences
    private lateinit var adManager: AdManager
    private lateinit var sharedKeyStore: SharedKeyStore
    private lateinit var backupRegistryStore: BackupRegistryStore
    private lateinit var encryptedBackupService: EncryptedBackupService

    fun init(context: Context) {
        val appContext = context.applicationContext
        sessionManager = VaultSessionManager(appContext)
        vaultService = VaultService(sessionManager)
        panicWipeService = PanicWipeService(appContext, sessionManager)
        vaultAuthService = VaultAuthService(appContext)
        vaultAuthController = VaultAuthController(vaultAuthService, vaultService)
        appPreferences = AppPreferences(appContext)
        adPreferences = AdPreferences(appContext)
        adManager = AdManager(adPreferences)
        sharedKeyStore = SharedKeyStore(appContext)
        backupRegistryStore = BackupRegistryStore(appContext)
        encryptedBackupService = EncryptedBackupService(vaultService, sharedKeyStore, backupRegistryStore)
    }

    fun sessionManager(): VaultSessionManager = sessionManager
    fun vaultService(): VaultService = vaultService
    fun panicWipeService(): PanicWipeService = panicWipeService
    fun vaultAuthService(): VaultAuthService = vaultAuthService
    fun vaultAuthController(): VaultAuthController = vaultAuthController
    fun appPreferences(): AppPreferences = appPreferences
    fun adPreferences(): AdPreferences = adPreferences
    fun adManager(): AdManager = adManager
    fun sharedKeyStore(): SharedKeyStore = sharedKeyStore
    fun backupRegistryStore(): BackupRegistryStore = backupRegistryStore
    fun encryptedBackupService(): EncryptedBackupService = encryptedBackupService
}

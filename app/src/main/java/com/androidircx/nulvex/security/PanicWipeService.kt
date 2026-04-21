package com.androidircx.nulvex.security

import android.content.Context
import androidx.work.WorkManager
import com.androidircx.nulvex.data.VaultSessionManager

class PanicWipeService(
    private val context: Context,
    private val sessionManager: VaultSessionManager,
    private val profiles: List<VaultProfile> = listOf(VaultProfile.REAL, VaultProfile.DECOY)
) {
    fun wipeAll() {
        sessionManager.close()
        WorkManager.getInstance(context).cancelUniqueWork("nulvex_self_destruct_sweep")
        for (profile in profiles) {
            context.deleteDatabase(profile.dbName)
            context.deleteSharedPreferences(profile.prefsName)
            context.deleteSharedPreferences(profile.keystorePrefsName)
            KeystoreSecretProvider(profile).deleteSecret()
        }
    }

    fun wipeDecoyOnly() {
        val profile = VaultProfile.DECOY
        if (sessionManager.getActiveProfile() == profile) {
            sessionManager.close()
        }
        context.deleteDatabase(profile.dbName)
        context.deleteSharedPreferences(profile.prefsName)
        context.deleteSharedPreferences(profile.keystorePrefsName)
        KeystoreSecretProvider(profile).deleteSecret()
    }
}

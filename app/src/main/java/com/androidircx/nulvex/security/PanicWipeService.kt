package com.androidircx.nulvex.security

import android.content.Context
import androidx.work.WorkManager
import com.androidircx.nulvex.data.VaultSessionManager
import java.io.File
import java.security.KeyStore

class PanicWipeService(
    private val context: Context,
    private val sessionManager: VaultSessionManager,
    private val profiles: List<VaultProfile> = listOf(VaultProfile.REAL, VaultProfile.DECOY)
) {
    fun wipeAll() {
        sessionManager.close()
        runCatching {
            WorkManager.getInstance(context).cancelUniqueWork("nulvex_self_destruct_sweep")
        }

        // Per-profile vault material: DB, key salts, keystore-wrapped secret.
        for (profile in profiles) {
            runCatching { context.deleteDatabase(profile.dbName) }
            deletePrefs(profile.prefsName)
            deletePrefs(profile.keystorePrefsName)
            runCatching { KeystoreSecretProvider(profile).deleteSecret() }
        }

        // Global secret stores that used to survive a "panic wipe": PIN hashes,
        // sharing keys, biometric-wrapped master keys, the security event log,
        // sync tokens, app settings, backup registry — plus their Tink keysets.
        GLOBAL_PREFS.forEach { deletePrefs(it) }
        GLOBAL_KEYSTORE_ALIASES.forEach { deleteKeystoreAlias(it) }

        // Encrypted attachment blobs on the filesystem.
        runCatching { File(context.filesDir, "attachments").deleteRecursively() }
    }

    fun wipeDecoyOnly() {
        val profile = VaultProfile.DECOY
        if (sessionManager.getActiveProfile() == profile) {
            sessionManager.close()
        }
        runCatching { context.deleteDatabase(profile.dbName) }
        deletePrefs(profile.prefsName)
        deletePrefs(profile.keystorePrefsName)
        runCatching { KeystoreSecretProvider(profile).deleteSecret() }
        // Also remove the decoy biometric material so a "disabled" decoy leaves no proof.
        deletePrefs(DECOY_BIOMETRIC_PREFS)
        deletePrefs("${DECOY_BIOMETRIC_PREFS}_tink_keyset")
        deleteKeystoreAlias(DECOY_BIOMETRIC_ALIAS)
        runCatching { File(File(context.filesDir, "attachments"), profile.id).deleteRecursively() }
    }

    private fun deletePrefs(name: String) {
        runCatching { context.deleteSharedPreferences(name) }
    }

    private fun deleteKeystoreAlias(alias: String) {
        runCatching {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
            }
        }
    }

    companion object {
        private const val DECOY_BIOMETRIC_PREFS = "nulvex_biometric_decoy"
        private const val DECOY_BIOMETRIC_ALIAS = "nulvex_biometric_decoy_key"

        private val GLOBAL_PREFS = listOf(
            "nulvex_auth_prefs",
            "nulvex_shared_keys",
            "nulvex_biometric",
            "nulvex_biometric_decoy",
            "nulvex_app_settings",
            "nulvex_backup_registry",
            // SecureTypedPreferences-backed stores + their Tink keyset side files.
            "nulvex_security_events",
            "nulvex_security_events_v2",
            "nulvex_security_events_v2_tink_keyset",
            "nulvex_sync_prefs",
            "nulvex_sync_secure_prefs",
            "nulvex_sync_secure_prefs_v2",
            "nulvex_sync_secure_prefs_v2_tink_keyset"
        )

        private val GLOBAL_KEYSTORE_ALIASES = listOf(
            "nulvex_shared_keys_aes",
            "nulvex_biometric_key",
            "nulvex_biometric_decoy_key"
        )
    }
}

package com.androidircx.nulvex.security

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import java.security.SecureRandom

class VaultPreferences(context: Context, profile: VaultProfile = VaultProfile.REAL) {
    private val prefs = context.getSharedPreferences(profile.prefsName, Context.MODE_PRIVATE)
    private val argonSaltKey = "argon_salt"
    private val dbSaltKey = "db_salt"

    fun getOrCreateArgonSalt(): ByteArray {
        return getOrCreateSalt(argonSaltKey)
    }

    fun getOrCreateDbSalt(): ByteArray {
        return getOrCreateSalt(dbSaltKey)
    }

    private fun getOrCreateSalt(key: String): ByteArray = synchronized(CryptoProvisioning.lock) {
        val existing = prefs.getString(key, null)
        if (existing != null) {
            return Base64.decode(existing, Base64.NO_WRAP)
        }
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        // commit (not apply): the derived master/db key is used immediately to
        // create the DB and to wrap the biometric key, so the salt must be durably
        // persisted before we return — otherwise process death mid-provisioning
        // regenerates a divergent salt on next launch.
        prefs.edit(commit = true) { putString(key, Base64.encodeToString(salt, Base64.NO_WRAP)) }
        return salt
    }
}

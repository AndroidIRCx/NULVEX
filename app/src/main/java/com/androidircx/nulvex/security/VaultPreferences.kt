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

    private fun getOrCreateSalt(key: String): ByteArray {
        val existing = prefs.getString(key, null)
        if (existing != null) {
            return Base64.decode(existing, Base64.NO_WRAP)
        }
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        prefs.edit { putString(key, Base64.encodeToString(salt, Base64.NO_WRAP)) }
        return salt
    }
}

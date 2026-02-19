package com.androidircx.nulvex.security

import android.content.Context

class VaultKeyManager(
    private val context: Context,
    private val profile: VaultProfile = VaultProfile.REAL,
    private val kdf: Kdf = Argon2idKdf(),
    private val prefs: VaultPreferences = VaultPreferences(context, profile),
    private val keystoreSecretProvider: KeystoreSecretProvider = KeystoreSecretProvider(profile)
) {
    fun deriveMasterKey(pin: CharArray): ByteArray {
        val salt = prefs.getOrCreateArgonSalt()
        val masterSeed = kdf.deriveKey(pin, salt, Argon2Params.DEFAULT)
        val keystoreSecret = keystoreSecretProvider.getOrCreateSecret(context)
        val ikm = masterSeed + keystoreSecret
        return Hkdf.deriveKey(
            ikm = ikm,
            salt = "NulvexMasterKey".toByteArray(),
            info = "master-key".toByteArray(),
            length = 32
        )
    }

    fun deriveDbKey(masterKey: ByteArray): ByteArray {
        val dbSalt = prefs.getOrCreateDbSalt()
        return Hkdf.deriveKey(
            ikm = masterKey,
            salt = dbSalt,
            info = "db-key".toByteArray(),
            length = 32
        )
    }

    fun deriveNoteKey(masterKey: ByteArray): ByteArray {
        return Hkdf.deriveKey(
            ikm = masterKey,
            salt = "NulvexNoteKey".toByteArray(),
            info = "note-key".toByteArray(),
            length = 32
        )
    }
}

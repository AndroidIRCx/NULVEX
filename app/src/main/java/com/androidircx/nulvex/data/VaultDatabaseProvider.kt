package com.androidircx.nulvex.data

import android.content.Context
import com.androidircx.nulvex.security.VaultKeyManager
import com.androidircx.nulvex.security.VaultProfile

class VaultDatabaseProvider(
    private val context: Context,
    private val profile: VaultProfile = VaultProfile.REAL,
    private val keyManager: VaultKeyManager = VaultKeyManager(context, profile)
) {
    fun openSession(pin: CharArray): VaultSession {
        val masterKey = keyManager.deriveMasterKey(pin)
        val dbKey = keyManager.deriveDbKey(masterKey)
        val noteKey = keyManager.deriveNoteKey(masterKey)
        val database = NulvexDatabaseFactory.buildEncrypted(context, dbKey, profile.dbName)
        return VaultSession(database = database, noteKey = noteKey)
    }
}

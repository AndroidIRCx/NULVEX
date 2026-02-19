package com.androidircx.nulvex.security

import com.androidircx.nulvex.data.NulvexDatabaseFactory

data class VaultProfile(
    val id: String,
    val dbName: String,
    val prefsName: String,
    val keystorePrefsName: String,
    val keystoreAlias: String
) {
    companion object {
        val REAL = VaultProfile(
            id = "real",
            dbName = NulvexDatabaseFactory.DB_NAME,
            prefsName = "nulvex_vault_prefs",
            keystorePrefsName = "nulvex_keystore_prefs",
            keystoreAlias = "nulvex_keystore_aes"
        )
        val DECOY = VaultProfile(
            id = "decoy",
            dbName = NulvexDatabaseFactory.DECOY_DB_NAME,
            prefsName = "nulvex_vault_prefs_decoy",
            keystorePrefsName = "nulvex_keystore_prefs_decoy",
            keystoreAlias = "nulvex_keystore_aes_decoy"
        )
    }
}

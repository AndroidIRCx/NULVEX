package com.androidircx.nulvex.security

import android.content.Context

class VaultAuthPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("nulvex_auth_prefs", Context.MODE_PRIVATE)

    fun getRealPinHash(): String? = prefs.getString("real_pin_hash", null)

    fun setRealPinHash(hash: String) {
        prefs.edit().putString("real_pin_hash", hash).apply()
    }

    fun getDecoyPinHash(): String? = prefs.getString("decoy_pin_hash", null)

    fun setDecoyPinHash(hash: String) {
        prefs.edit().putString("decoy_pin_hash", hash).apply()
    }

    fun clearDecoyPinHash() {
        prefs.edit().remove("decoy_pin_hash").apply()
    }
}

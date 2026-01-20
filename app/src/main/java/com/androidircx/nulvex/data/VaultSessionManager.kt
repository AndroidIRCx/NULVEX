package com.androidircx.nulvex.data

import android.content.Context
import com.androidircx.nulvex.security.VaultProfile

class VaultSessionManager(
    val context: Context
) {
    private var session: VaultSession? = null
    private var activeProfile: VaultProfile? = null
    private val providers = mutableMapOf<String, VaultDatabaseProvider>()

    @Synchronized
    fun open(pin: CharArray, profile: VaultProfile = VaultProfile.REAL): VaultSession {
        close()
        val provider = providers.getOrPut(profile.id) {
            VaultDatabaseProvider(context, profile)
        }
        val newSession = provider.openSession(pin)
        session = newSession
        activeProfile = profile
        return newSession
    }

    @Synchronized
    fun openWithMasterKey(masterKey: ByteArray, profile: VaultProfile = VaultProfile.REAL): VaultSession {
        close()
        val provider = providers.getOrPut(profile.id) {
            VaultDatabaseProvider(context, profile)
        }
        val newSession = provider.openSessionWithMasterKey(masterKey)
        session = newSession
        activeProfile = profile
        return newSession
    }

    @Synchronized
    fun getActive(): VaultSession? = session

    @Synchronized
    fun getActiveProfile(): VaultProfile? = activeProfile

    @Synchronized
    fun close() {
        session?.close()
        session = null
        activeProfile = null
    }
}

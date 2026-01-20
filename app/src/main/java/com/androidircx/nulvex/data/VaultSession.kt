package com.androidircx.nulvex.data

import com.androidircx.nulvex.security.wipe

data class VaultSession(
    val database: NulvexDatabase,
    val noteKey: ByteArray
) {
    fun close() {
        noteKey.wipe()
        database.close()
    }
}

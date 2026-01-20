package com.androidircx.nulvex.data

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object NulvexDatabaseFactory {
    const val DB_NAME = "nulvex.db"
    const val DECOY_DB_NAME = "nulvex_decoy.db"

    fun buildEncrypted(
        context: Context,
        passphrase: ByteArray,
        dbName: String = DB_NAME
    ): NulvexDatabase {
        SQLiteDatabase.loadLibs(context)
        val factory = SupportFactory(passphrase)
        return Room.databaseBuilder(context, NulvexDatabase::class.java, dbName)
            .openHelperFactory(factory)
            .build()
    }
}

package com.androidircx.nulvex.data

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

object NulvexDatabaseFactory {
    const val DB_NAME = "nulvex.db"
    const val DECOY_DB_NAME = "nulvex_decoy.db"

    fun buildEncrypted(
        context: Context,
        passphrase: ByteArray,
        dbName: String = DB_NAME
    ): NulvexDatabase {
        System.loadLibrary("sqlcipher")
        val factory = SupportOpenHelperFactory(passphrase)
        return Room.databaseBuilder(context, NulvexDatabase::class.java, dbName)
            .openHelperFactory(factory)
            .addMigrations(
                DatabaseMigrations.MIGRATION_1_2,
                DatabaseMigrations.MIGRATION_2_3
            )
            .build()
    }
}

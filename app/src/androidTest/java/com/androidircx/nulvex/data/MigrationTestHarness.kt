package com.androidircx.nulvex.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import java.io.File

object MigrationTestHarness {
    fun createLegacyV1Database(context: Context, dbName: String): SupportSQLiteDatabase {
        deleteIfExists(context, dbName)
        val configuration = androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    createV1Schema(db)
                }

                override fun onUpgrade(
                    db: SupportSQLiteDatabase,
                    oldVersion: Int,
                    newVersion: Int
                ) = Unit
            })
            .build()
        val helper = FrameworkSQLiteOpenHelperFactory().create(configuration)
        return helper.writableDatabase
    }

    fun deleteIfExists(context: Context, dbName: String) {
        val dbFile = context.getDatabasePath(dbName)
        if (dbFile.exists()) {
            dbFile.delete()
        }
        val walFile = File("${dbFile.path}-wal")
        if (walFile.exists()) walFile.delete()
        val shmFile = File("${dbFile.path}-shm")
        if (shmFile.exists()) shmFile.delete()
    }

    private fun createV1Schema(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS notes (
                id TEXT NOT NULL PRIMARY KEY,
                ciphertext BLOB NOT NULL,
                createdAt INTEGER NOT NULL,
                expiresAt INTEGER,
                readOnce INTEGER NOT NULL,
                deleted INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_expiresAt ON notes(expiresAt)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_deleted ON notes(deleted)")
    }
}

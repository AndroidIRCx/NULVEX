package com.androidircx.nulvex.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN archivedAt INTEGER")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_archivedAt ON notes(archivedAt)")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN reminderAt INTEGER")
            db.execSQL("ALTER TABLE notes ADD COLUMN reminderDone INTEGER NOT NULL DEFAULT 0")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_reminderAt ON notes(reminderAt)")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS note_revisions (
                    id TEXT NOT NULL PRIMARY KEY,
                    noteId TEXT NOT NULL,
                    ciphertextSnapshot BLOB NOT NULL,
                    expiresAt INTEGER,
                    readOnce INTEGER NOT NULL,
                    archivedAt INTEGER,
                    reminderAt INTEGER,
                    reminderDone INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_note_revisions_noteId_createdAt ON note_revisions(noteId, createdAt)")
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN trashedAt INTEGER")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_notes_trashedAt ON notes(trashedAt)")
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
            db.execSQL("UPDATE notes SET updatedAt = createdAt WHERE updatedAt = 0")
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE notes ADD COLUMN reminderRepeat TEXT")
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sync_outbox (
                    opId TEXT NOT NULL PRIMARY KEY,
                    deviceId TEXT NOT NULL,
                    profile TEXT NOT NULL,
                    entityType TEXT NOT NULL,
                    entityId TEXT NOT NULL,
                    opType TEXT NOT NULL,
                    baseRevision TEXT,
                    envelopeCiphertext BLOB NOT NULL,
                    clientTs INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    attemptCount INTEGER NOT NULL DEFAULT 0,
                    nextAttemptAt INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_outbox_nextAttemptAt ON sync_outbox(nextAttemptAt)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_outbox_profile_createdAt ON sync_outbox(profile, createdAt)")

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sync_cursor (
                    profile TEXT NOT NULL PRIMARY KEY,
                    cursorToken TEXT NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sync_conflicts (
                    id TEXT NOT NULL PRIMARY KEY,
                    profile TEXT NOT NULL,
                    entityId TEXT NOT NULL,
                    localRevision TEXT,
                    remoteRevision TEXT,
                    remoteOpId TEXT,
                    resolutionPolicy TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    resolvedAt INTEGER
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_conflicts_profile_entityId ON sync_conflicts(profile, entityId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_sync_conflicts_resolvedAt ON sync_conflicts(resolvedAt)")
        }
    }
}

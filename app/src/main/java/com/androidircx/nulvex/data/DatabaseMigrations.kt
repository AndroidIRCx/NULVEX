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
}

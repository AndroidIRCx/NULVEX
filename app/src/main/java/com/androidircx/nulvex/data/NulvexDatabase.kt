package com.androidircx.nulvex.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        NoteEntity::class,
        NoteRevisionEntity::class,
        SyncOutboxEntity::class,
        SyncCursorEntity::class,
        SyncConflictEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class NulvexDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun syncStateDao(): SyncStateDao
}

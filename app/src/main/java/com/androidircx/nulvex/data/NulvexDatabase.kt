package com.androidircx.nulvex.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class],
    version = 3,
    exportSchema = false
)
abstract class NulvexDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

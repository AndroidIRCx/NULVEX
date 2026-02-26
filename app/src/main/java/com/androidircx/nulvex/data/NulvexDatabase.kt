package com.androidircx.nulvex.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class, NoteRevisionEntity::class],
    version = 7,
    exportSchema = false
)
abstract class NulvexDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

package com.androidircx.nulvex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity): Long

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE deleted = 0 AND archivedAt IS NULL")
    suspend fun listActive(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE deleted = 0 AND archivedAt IS NOT NULL")
    suspend fun listArchived(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE expiresAt IS NOT NULL AND expiresAt <= :now AND deleted = 0")
    suspend fun listExpired(now: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE reminderAt IS NOT NULL AND reminderAt <= :now AND reminderDone = 0 AND deleted = 0")
    suspend fun listDueReminders(now: Long): List<NoteEntity>

    @Query("UPDATE notes SET ciphertext = :ciphertext WHERE id = :id")
    suspend fun overwriteCiphertext(id: String, ciphertext: ByteArray): Int

    @Query("UPDATE notes SET deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String): Int

    @Query("UPDATE notes SET archivedAt = :archivedAt WHERE id = :id")
    suspend fun setArchivedAt(id: String, archivedAt: Long?): Int

    @Query("UPDATE notes SET reminderAt = :reminderAt, reminderDone = :reminderDone WHERE id = :id")
    suspend fun setReminder(id: String, reminderAt: Long?, reminderDone: Boolean): Int

    @Query("DELETE FROM notes WHERE deleted = 1")
    suspend fun purgeDeleted(): Int
}

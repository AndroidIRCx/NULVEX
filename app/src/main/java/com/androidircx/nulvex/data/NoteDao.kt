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

    @Query("SELECT * FROM notes WHERE deleted = 0")
    suspend fun listActive(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE expiresAt IS NOT NULL AND expiresAt <= :now AND deleted = 0")
    suspend fun listExpired(now: Long): List<NoteEntity>

    @Query("UPDATE notes SET ciphertext = :ciphertext WHERE id = :id")
    suspend fun overwriteCiphertext(id: String, ciphertext: ByteArray): Int

    @Query("UPDATE notes SET deleted = 1 WHERE id = :id")
    suspend fun softDelete(id: String): Int

    @Query("DELETE FROM notes WHERE deleted = 1")
    suspend fun purgeDeleted(): Int
}

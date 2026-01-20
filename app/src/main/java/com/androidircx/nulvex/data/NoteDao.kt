package com.androidircx.nulvex.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(note: NoteEntity): Long

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getById(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE deleted = 0")
    fun listActive(): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE expiresAt IS NOT NULL AND expiresAt <= :now AND deleted = 0")
    fun listExpired(now: Long): List<NoteEntity>

    @Query("UPDATE notes SET ciphertext = :ciphertext WHERE id = :id")
    fun overwriteCiphertext(id: String, ciphertext: ByteArray): Int

    @Query("UPDATE notes SET deleted = 1 WHERE id = :id")
    fun softDelete(id: String): Int

    @Query("DELETE FROM notes WHERE deleted = 1")
    fun purgeDeleted(): Int
}

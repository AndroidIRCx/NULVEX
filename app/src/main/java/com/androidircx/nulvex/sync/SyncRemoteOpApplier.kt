package com.androidircx.nulvex.sync

import com.androidircx.nulvex.data.NoteDao
import com.androidircx.nulvex.data.NoteEntity
import org.json.JSONObject
import java.util.Base64

class SyncRemoteOpApplier(
    private val noteDao: NoteDao,
    private val onRemotePanicWipe: suspend () -> Unit
) {
    suspend fun apply(op: SyncPulledOp): Boolean {
        return when (val decoded = decode(op)) {
            is DecodedRemoteOp.Noop -> true
            is DecodedRemoteOp.Upsert -> {
                noteDao.upsert(decoded.entity)
                true
            }
            is DecodedRemoteOp.Delete -> {
                applyDelete(decoded.entityId)
                true
            }
            is DecodedRemoteOp.RemotePanicWipe -> {
                onRemotePanicWipe()
                false
            }
        }
    }

    private suspend fun applyDelete(entityId: String) {
        val existing = noteDao.getById(entityId) ?: return
        noteDao.overwriteCiphertext(entityId, ByteArray(existing.ciphertext.size))
        noteDao.softDelete(entityId)
        noteDao.setUpdatedAt(entityId, System.currentTimeMillis())
    }

    internal fun decode(op: SyncPulledOp, now: Long = System.currentTimeMillis()): DecodedRemoteOp {
        val raw = op.ciphertext.toString(Charsets.UTF_8).trim()
        if (raw.isEmpty()) return DecodedRemoteOp.Noop
        val payload = runCatching { JSONObject(raw) }.getOrNull() ?: return DecodedRemoteOp.Noop
        val opType = payload.optString("op_type", "").trim().lowercase()
        if (opType == "panic_wipe" || payload.optBoolean("panic_wipe")) {
            return DecodedRemoteOp.RemotePanicWipe
        }

        val entityId = payload.optString("entity_id", op.entityId).ifBlank { op.entityId }
        if (opType == "delete" || payload.optBoolean("deleted")) {
            return DecodedRemoteOp.Delete(entityId)
        }

        val notePayload = payload.optJSONObject("note") ?: return DecodedRemoteOp.Noop
        val ciphertext = decodeCiphertext(notePayload) ?: return DecodedRemoteOp.Noop
        val createdAt = notePayload.optLong("created_at", now)
        val updatedAt = notePayload.optLong("updated_at", createdAt)
        return DecodedRemoteOp.Upsert(
            NoteEntity(
                id = entityId,
                ciphertext = ciphertext,
                createdAt = createdAt,
                expiresAt = notePayload.optNullableLong("expires_at"),
                readOnce = notePayload.optBoolean("read_once", false),
                deleted = notePayload.optBoolean("deleted", false),
                archivedAt = notePayload.optNullableLong("archived_at"),
                reminderAt = notePayload.optNullableLong("reminder_at"),
                reminderDone = notePayload.optBoolean("reminder_done", false),
                reminderRepeat = notePayload.optNullableString("reminder_repeat"),
                trashedAt = notePayload.optNullableLong("trashed_at"),
                updatedAt = updatedAt
            )
        )
    }

    private fun decodeCiphertext(notePayload: JSONObject): ByteArray? {
        val b64 = notePayload.optString("ciphertext_b64", "")
        if (b64.isBlank()) return null
        return runCatching { Base64.getDecoder().decode(b64) }.getOrNull()
    }

    private fun JSONObject.optNullableLong(key: String): Long? {
        if (!has(key) || isNull(key)) return null
        return optLong(key)
    }

    private fun JSONObject.optNullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return optString(key).takeIf { it.isNotBlank() }
    }
}

internal sealed interface DecodedRemoteOp {
    data object Noop : DecodedRemoteOp
    data class Upsert(val entity: NoteEntity) : DecodedRemoteOp
    data class Delete(val entityId: String) : DecodedRemoteOp
    data object RemotePanicWipe : DecodedRemoteOp
}

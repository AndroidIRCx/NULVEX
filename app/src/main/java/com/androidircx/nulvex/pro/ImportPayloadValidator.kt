package com.androidircx.nulvex.pro

import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImportPayloadValidator {
    // Per-tier caps. Free users get the smaller limits; Pro (pro_features_lifetime) the
    // larger ones. NOTE_SHARE_MAX_BYTES / BACKUP_MAX_BYTES keep the Pro (maximum) value so
    // they double as the absolute hard cap for the raw file-read paths.
    const val NOTE_SHARE_MAX_BYTES_FREE: Int = 10 * 1024 * 1024
    const val NOTE_SHARE_MAX_BYTES: Int = 50 * 1024 * 1024 // Pro / hard max
    const val BACKUP_MAX_BYTES_FREE: Int = 50 * 1024 * 1024
    const val BACKUP_MAX_BYTES: Int = 1024 * 1024 * 1024 // 1 GB, Pro / hard max
    const val KEY_MANAGER_MAX_BYTES: Int = 2 * 1024 * 1024

    private val proMaxByMime: Map<String, Int> = mapOf(
        NulvexFileTypes.NOTE_SHARE_MIME to NOTE_SHARE_MAX_BYTES,
        NulvexFileTypes.BACKUP_MIME to BACKUP_MAX_BYTES,
        NulvexFileTypes.KEY_MANAGER_MIME to KEY_MANAGER_MAX_BYTES
    )

    private val freeMaxByMime: Map<String, Int> = mapOf(
        NulvexFileTypes.NOTE_SHARE_MIME to NOTE_SHARE_MAX_BYTES_FREE,
        NulvexFileTypes.BACKUP_MIME to BACKUP_MAX_BYTES_FREE,
        NulvexFileTypes.KEY_MANAGER_MIME to KEY_MANAGER_MAX_BYTES
    )

    fun isSupportedMime(mimeType: String): Boolean {
        return proMaxByMime.containsKey(normalizeMime(mimeType))
    }

    // isPro defaults to true so the raw file-read paths use the absolute hard cap; the
    // tier-specific limit is enforced explicitly at import validation with the real tier.
    fun maxBytesForMime(mimeType: String, isPro: Boolean = true): Int {
        val normalized = normalizeMime(mimeType)
        val table = if (isPro) proMaxByMime else freeMaxByMime
        return table[normalized] ?: throw UnsupportedImportMimeException(normalized)
    }

    fun validateSizeOrThrow(sizeBytes: Int, mimeType: String, isPro: Boolean = true) {
        val maxBytes = maxBytesForMime(mimeType, isPro)
        if (sizeBytes > maxBytes) {
            throw PayloadTooLargeException(
                mimeType = normalizeMime(mimeType),
                sizeBytes = sizeBytes.toLong(),
                maxBytes = maxBytes
            )
        }
    }

    fun readWithLimit(input: InputStream, mimeType: String, isPro: Boolean = true): ByteArray {
        val maxBytes = maxBytesForMime(mimeType, isPro)
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var totalBytes = 0L

        while (true) {
            val read = input.read(buffer)
            if (read <= 0) break
            totalBytes += read.toLong()
            if (totalBytes > maxBytes.toLong()) {
                throw PayloadTooLargeException(
                    mimeType = normalizeMime(mimeType),
                    sizeBytes = totalBytes,
                    maxBytes = maxBytes
                )
            }
            output.write(buffer, 0, read)
        }

        return output.toByteArray()
    }

    private fun normalizeMime(mimeType: String): String {
        return mimeType.trim().lowercase()
    }
}

class UnsupportedImportMimeException(mimeType: String) :
    IllegalArgumentException("Unsupported import MIME type: $mimeType")

class PayloadTooLargeException(
    mimeType: String,
    sizeBytes: Long,
    maxBytes: Int
) : IllegalArgumentException(
    "Payload too large for MIME type $mimeType ($sizeBytes bytes > $maxBytes bytes)"
)

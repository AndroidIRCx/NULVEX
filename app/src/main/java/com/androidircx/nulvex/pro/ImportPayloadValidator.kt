package com.androidircx.nulvex.pro

import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImportPayloadValidator {
    const val NOTE_SHARE_MAX_BYTES: Int = 5 * 1024 * 1024
    const val BACKUP_MAX_BYTES: Int = 50 * 1024 * 1024
    const val KEY_MANAGER_MAX_BYTES: Int = 2 * 1024 * 1024

    private val maxBytesByMime: Map<String, Int> = mapOf(
        NulvexFileTypes.NOTE_SHARE_MIME to NOTE_SHARE_MAX_BYTES,
        NulvexFileTypes.BACKUP_MIME to BACKUP_MAX_BYTES,
        NulvexFileTypes.KEY_MANAGER_MIME to KEY_MANAGER_MAX_BYTES
    )

    fun isSupportedMime(mimeType: String): Boolean {
        return maxBytesByMime.containsKey(normalizeMime(mimeType))
    }

    fun maxBytesForMime(mimeType: String): Int {
        val normalized = normalizeMime(mimeType)
        return maxBytesByMime[normalized] ?: throw UnsupportedImportMimeException(normalized)
    }

    fun validateSizeOrThrow(sizeBytes: Int, mimeType: String) {
        val maxBytes = maxBytesForMime(mimeType)
        if (sizeBytes > maxBytes) {
            throw PayloadTooLargeException(
                mimeType = normalizeMime(mimeType),
                sizeBytes = sizeBytes.toLong(),
                maxBytes = maxBytes
            )
        }
    }

    fun readWithLimit(input: InputStream, mimeType: String): ByteArray {
        val maxBytes = maxBytesForMime(mimeType)
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

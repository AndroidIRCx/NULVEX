package com.androidircx.nulvex.sync

import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Base64

class LaravelSyncApiClient(
    private val baseApiUrl: String = "https://androidircx.com/api"
) : SyncApi {

    override suspend fun registerDevice(profile: String, token: SyncAuthToken): Boolean {
        val endpoint = URL("$baseApiUrl/sync/v1/devices/register")
        val conn = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer ${token.accessToken}")
        }
        val body = JSONObject().apply {
            put("device_id", token.deviceId)
            put("profile", profile)
        }.toString()
        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }
        return conn.responseCode in 200..299
    }

    override suspend fun push(
        profile: String,
        token: SyncAuthToken,
        operations: List<SyncEnvelope>
    ): List<SyncPushAck> {
        val endpoint = URL("$baseApiUrl/sync/v1/ops/push")
        val conn = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer ${token.accessToken}")
        }
        val ops = JSONArray()
        operations.forEach { op ->
            ops.put(
                JSONObject().apply {
                    put("op_id", op.opId)
                    put("profile", op.profile)
                    put("entity_type", op.entityType)
                    put("entity_id", op.entityId)
                    put("op_type", op.opType)
                    put("base_revision", op.baseRevision ?: JSONObject.NULL)
                    put("client_ts", op.clientTs)
                    put("ciphertext_b64", Base64.getEncoder().encodeToString(op.ciphertext))
                }
            )
        }
        val body = JSONObject().apply {
            put("device_id", token.deviceId)
            put("profile", profile)
            put("operations", ops)
        }.toString()
        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { it.write(body) }

        val response = readJsonResponse(conn)
        val acks = response.optJSONArray("acks") ?: JSONArray()
        return buildList {
            for (i in 0 until acks.length()) {
                val item = acks.optJSONObject(i) ?: continue
                val opId = item.optString("op_id", "")
                if (opId.isBlank()) continue
                add(
                    SyncPushAck(
                        opId = opId,
                        accepted = item.optBoolean("accepted", false)
                    )
                )
            }
        }
    }

    override suspend fun pull(
        profile: String,
        token: SyncAuthToken,
        cursorToken: String?,
        limit: Int
    ): SyncPullResult {
        val query = buildList {
            add("profile=${URLEncoder.encode(profile, Charsets.UTF_8.name())}")
            add("limit=$limit")
            if (!cursorToken.isNullOrBlank()) {
                add("cursor=${URLEncoder.encode(cursorToken, Charsets.UTF_8.name())}")
            }
        }.joinToString("&")
        val endpoint = URL("$baseApiUrl/sync/v1/ops/pull?$query")
        val conn = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            setRequestProperty("Authorization", "Bearer ${token.accessToken}")
        }
        val response = readJsonResponse(conn)
        val ops = response.optJSONArray("operations") ?: JSONArray()
        val pulled = buildList {
            for (i in 0 until ops.length()) {
                val item = ops.optJSONObject(i) ?: continue
                val opId = item.optString("op_id", "")
                val entityId = item.optString("entity_id", "")
                if (opId.isBlank() || entityId.isBlank()) continue
                val ct = item.optString("ciphertext_b64", "")
                val decoded = if (ct.isBlank()) ByteArray(0) else Base64.getDecoder().decode(ct)
                add(
                    SyncPulledOp(
                        opId = opId,
                        entityId = entityId,
                        revision = item.optString("revision", "").ifBlank { null },
                        baseRevision = item.optString("base_revision", "").ifBlank { null },
                        ciphertext = decoded
                    )
                )
            }
        }
        return SyncPullResult(
            cursorToken = response.optString("cursor", "").ifBlank { null },
            operations = pulled
        )
    }

    private fun readJsonResponse(conn: HttpURLConnection): JSONObject {
        val code = conn.responseCode
        val text = if (code in 200..299) {
            conn.inputStream.use { it.reader(Charsets.UTF_8).readText() }
        } else {
            val body = conn.errorStream?.use { it.reader(Charsets.UTF_8).readText() } ?: "No body"
            throw IllegalStateException("Sync API request failed ($code): $body")
        }
        return if (text.isBlank()) JSONObject() else JSONObject(text)
    }

    companion object {
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 30_000
    }
}

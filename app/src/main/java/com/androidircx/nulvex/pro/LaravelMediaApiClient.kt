package com.androidircx.nulvex.pro

import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class UploadRequestToken(
    val id: String,
    val uploadToken: String,
    val expires: Long,
    val downloadToken: String? = null,
    val downloadExpires: Long? = null
)

class LaravelMediaApiClient(
    private val baseApiUrl: String = "https://androidircx.com/api"
) {
    fun requestUpload(type: String = "file", mime: String = "application/octet-stream"): UploadRequestToken {
        val endpoint = URL("$baseApiUrl/media/request-upload")
        val conn = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }

        val body = JSONObject().apply {
            put("type", type)
            put("mime", mime)
        }.toString()

        OutputStreamWriter(conn.outputStream, Charsets.UTF_8).use { writer ->
            writer.write(body)
        }

        val response = readResponse(conn)
        val json = JSONObject(response)
        return UploadRequestToken(
            id = json.getString("id"),
            uploadToken = json.getString("upload_token"),
            expires = json.getLong("expires"),
            downloadToken = json.optString("download_token", "").ifBlank { null },
            downloadExpires = json.optLong("download_expires").takeIf {
                json.has("download_expires") && !json.isNull("download_expires")
            }
        )
    }

    fun upload(id: String, token: String, expires: Long, payload: ByteArray): Boolean {
        val encodedToken = URLEncoder.encode(token, Charsets.UTF_8.name())
        val endpoint = URL("$baseApiUrl/media/upload/$id?token=$encodedToken&expires=$expires")
        val conn = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "PUT"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/octet-stream")
            setRequestProperty("X-Upload-Token", token)
            setRequestProperty("X-Upload-Expires", expires.toString())
            setRequestProperty("Content-Length", payload.size.toString())
        }

        BufferedOutputStream(conn.outputStream).use { out ->
            out.write(payload)
            out.flush()
        }

        val code = conn.responseCode
        if (code !in 200..299) {
            val body = readError(conn)
            throw IllegalStateException("Upload failed ($code): $body")
        }
        return true
    }

    fun download(id: String, downloadToken: String? = null, downloadExpires: Long? = null): ByteArray {
        val query = buildList {
            if (!downloadToken.isNullOrBlank()) {
                add("token=${URLEncoder.encode(downloadToken, Charsets.UTF_8.name())}")
            }
            if (downloadExpires != null) {
                add("expires=$downloadExpires")
            }
        }.joinToString("&")
        val endpoint = URL(
            if (query.isBlank()) "$baseApiUrl/media/download/$id"
            else "$baseApiUrl/media/download/$id?$query"
        )
        val conn = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            if (!downloadToken.isNullOrBlank()) {
                setRequestProperty("X-Download-Token", downloadToken)
            }
            if (downloadExpires != null) {
                setRequestProperty("X-Download-Expires", downloadExpires.toString())
            }
        }
        val code = conn.responseCode
        if (code !in 200..299) {
            val body = readError(conn)
            throw IllegalStateException("Download failed ($code): $body")
        }
        return conn.inputStream.use { it.readBytes() }
    }

    private fun readResponse(conn: HttpURLConnection): String {
        val code = conn.responseCode
        if (code !in 200..299) {
            val body = readError(conn)
            throw IllegalStateException("Request failed ($code): $body")
        }
        return conn.inputStream.use { it.reader(Charsets.UTF_8).readText() }
    }

    private fun readError(conn: HttpURLConnection): String {
        return conn.errorStream?.use { it.reader(Charsets.UTF_8).readText() } ?: "No body"
    }

    companion object {
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 30_000
    }
}

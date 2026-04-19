package com.androidircx.nulvex.sync

import com.sun.net.httpserver.HttpServer
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.InetSocketAddress
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.runBlocking

class LaravelSyncApiClientTest {
    private lateinit var server: HttpServer
    private lateinit var baseUrl: String

    @Before
    fun setUp() {
        server = HttpServer.create(InetSocketAddress(0), 0)
        server.start()
        baseUrl = "http://127.0.0.1:${server.address.port}"
    }

    @After
    fun tearDown() {
        server.stop(0)
    }

    @Test
    fun registerDevice_postsPayload() = runBlocking {
        val seenAuth = AtomicReference<String>("")
        server.createContext("/api/sync/v1/devices/register") { exchange ->
            seenAuth.set(exchange.requestHeaders.getFirst("Authorization") ?: "")
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.close()
        }
        val api = LaravelSyncApiClient("$baseUrl/api")
        val ok = api.registerDevice(
            profile = "real",
            token = SyncAuthToken("tkn", null, 1L, "dev-a"),
            requestSecurity = null
        )
        assertTrue(ok)
        assertEquals("Bearer tkn", seenAuth.get())
    }

    @Test
    fun push_parsesAcks() = runBlocking {
        server.createContext("/api/sync/v1/ops/push") { exchange ->
            val response = """{"acks":[{"op_id":"op-1","accepted":true},{"op_id":"op-2","accepted":false}]}"""
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        val api = LaravelSyncApiClient("$baseUrl/api")
        val acks = api.push(
            profile = "real",
            token = SyncAuthToken("tkn", null, 1L, "dev-a"),
            operations = listOf(
                SyncEnvelope(
                    opId = "op-1",
                    profile = "real",
                    entityType = "note",
                    entityId = "n-1",
                    opType = "upsert",
                    baseRevision = null,
                    clientTs = 1L,
                    ciphertext = byteArrayOf(1, 2)
                )
            ),
            requestSecurity = null
        )
        assertEquals(2, acks.size)
        assertTrue(acks.first { it.opId == "op-1" }.accepted)
        assertTrue(!acks.first { it.opId == "op-2" }.accepted)
    }

    @Test
    fun pull_parsesOperations() = runBlocking {
        val ct = Base64.getEncoder().encodeToString(byteArrayOf(9, 8, 7))
        server.createContext("/api/sync/v1/ops/pull") { exchange ->
            val response = """{"cursor":"cur-2","operations":[{"op_id":"rop-1","entity_id":"n-1","revision":"r2","base_revision":"r1","ciphertext_b64":"$ct"}]}"""
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }
        val api = LaravelSyncApiClient("$baseUrl/api")
        val pull = api.pull(
            profile = "real",
            token = SyncAuthToken("tkn", null, 1L, "dev-a"),
            cursorToken = "cur-1",
            limit = 20,
            requestSecurity = null
        )
        assertEquals("cur-2", pull.cursorToken)
        assertEquals(1, pull.operations.size)
        assertEquals("n-1", pull.operations.first().entityId)
        assertEquals(3, pull.operations.first().ciphertext.size)
    }

    @Test
    fun push_sendsIntegrityHeadersAndBody_whenSecurityProvided() = runBlocking {
        val seenTokenHeader = AtomicReference<String>("")
        val seenHashHeader = AtomicReference<String>("")
        val seenIssuedAtHeader = AtomicReference<String>("")
        val seenBodyIntegrityToken = AtomicReference<String>("")
        val seenBodyHash = AtomicReference<String>("")

        server.createContext("/api/sync/v1/ops/push") { exchange ->
            seenTokenHeader.set(exchange.requestHeaders.getFirst("X-Play-Integrity-Token") ?: "")
            seenHashHeader.set(exchange.requestHeaders.getFirst("X-Play-Integrity-Request-Hash") ?: "")
            seenIssuedAtHeader.set(exchange.requestHeaders.getFirst("X-Play-Integrity-Issued-At") ?: "")

            val rawBody = exchange.requestBody.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val body = JSONObject(rawBody)
            val sec = body.getJSONObject("request_security")
            seenBodyIntegrityToken.set(sec.getString("integrity_token"))
            seenBodyHash.set(sec.getString("request_hash"))

            val response = """{"acks":[{"op_id":"op-1","accepted":true}]}"""
            exchange.sendResponseHeaders(200, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }

        val api = LaravelSyncApiClient("$baseUrl/api")
        val security = SyncRequestSecurity(
            integrityToken = "pi-token-123",
            requestHash = "hash-abc",
            issuedAtEpochSeconds = 1700000000L
        )
        val acks = api.push(
            profile = "real",
            token = SyncAuthToken("tkn", null, 1L, "dev-a"),
            operations = listOf(
                SyncEnvelope(
                    opId = "op-1",
                    profile = "real",
                    entityType = "note",
                    entityId = "n-1",
                    opType = "upsert",
                    baseRevision = null,
                    clientTs = 1L,
                    ciphertext = byteArrayOf(1, 2)
                )
            ),
            requestSecurity = security
        )

        assertEquals(1, acks.size)
        assertEquals("pi-token-123", seenTokenHeader.get())
        assertEquals("hash-abc", seenHashHeader.get())
        assertEquals("1700000000", seenIssuedAtHeader.get())
        assertEquals("pi-token-123", seenBodyIntegrityToken.get())
        assertEquals("hash-abc", seenBodyHash.get())
    }

    @Test
    fun pull_error_doesNotLeakBackendBody() = runBlocking {
        server.createContext("/api/sync/v1/ops/pull") { exchange ->
            val response = """{"error":"secret_token_should_not_leak"}"""
            exchange.sendResponseHeaders(401, response.toByteArray().size.toLong())
            exchange.responseBody.use { it.write(response.toByteArray()) }
        }

        val api = LaravelSyncApiClient("$baseUrl/api")
        val thrown = org.junit.Assert.assertThrows(IllegalStateException::class.java) {
            runBlocking {
                api.pull(
                    profile = "real",
                    token = SyncAuthToken("tkn", null, 1L, "dev-a"),
                    cursorToken = null,
                    limit = 20,
                    requestSecurity = null
                )
            }
        }
        val message = thrown.message.orEmpty()
        assertTrue(message.contains("Sync API request failed (401)"))
        assertTrue(!message.contains("secret_token_should_not_leak"))
    }
}

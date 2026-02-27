package com.androidircx.nulvex.sync

import com.sun.net.httpserver.HttpServer
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
            token = SyncAuthToken("tkn", null, 1L, "dev-a")
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
            )
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
            limit = 20
        )
        assertEquals("cur-2", pull.cursorToken)
        assertEquals(1, pull.operations.size)
        assertEquals("n-1", pull.operations.first().entityId)
        assertEquals(3, pull.operations.first().ciphertext.size)
    }
}

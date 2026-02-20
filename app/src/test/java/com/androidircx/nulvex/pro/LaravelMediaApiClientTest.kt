package com.androidircx.nulvex.pro

import com.sun.net.httpserver.HttpServer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicReference

class LaravelMediaApiClientTest {

    private lateinit var server: HttpServer
    private lateinit var baseUrl: String

    @Before
    fun setUp() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.executor = null
        server.start()
        baseUrl = "http://127.0.0.1:${server.address.port}/api"
    }

    @After
    fun tearDown() {
        server.stop(0)
    }

    @Test
    fun requestUpload_parsesResponse() {
        val requestBody = AtomicReference("")
        server.createContext("/api/media/request-upload") { exchange ->
            requestBody.set(exchange.requestBody.readBytes().toString(Charsets.UTF_8))
            val response = """
                {"id":"media-1","upload_token":"up-tok","expires":123,"download_token":"dl-tok","download_expires":456}
            """.trimIndent().toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { it.write(response) }
        }

        val client = LaravelMediaApiClient(baseUrl)
        val token = client.requestUpload(type = "file", mime = "application/test")

        assertEquals("media-1", token.id)
        assertEquals("up-tok", token.uploadToken)
        assertEquals(123L, token.expires)
        assertEquals("dl-tok", token.downloadToken)
        assertEquals(456L, token.downloadExpires)
        assertTrue(requestBody.get().contains("\"type\":\"file\""))
        assertTrue(requestBody.get().contains("\"mime\":\"application/test\""))
    }

    @Test
    fun upload_sendsPayloadAndHeaders() {
        val seenQuery = AtomicReference("")
        val seenTokenHeader = AtomicReference("")
        val seenExpiresHeader = AtomicReference("")
        val seenBody = AtomicReference<ByteArray>(byteArrayOf())
        server.createContext("/api/media/upload/abc") { exchange ->
            seenQuery.set(exchange.requestURI.query ?: "")
            seenTokenHeader.set(exchange.requestHeaders.getFirst("X-Upload-Token") ?: "")
            seenExpiresHeader.set(exchange.requestHeaders.getFirst("X-Upload-Expires") ?: "")
            seenBody.set(exchange.requestBody.readBytes())
            exchange.sendResponseHeaders(200, 0)
            exchange.responseBody.close()
        }

        val client = LaravelMediaApiClient(baseUrl)
        val payload = "hello-upload".toByteArray()
        val ok = client.upload("abc", "tok+1", 999L, payload)

        assertTrue(ok)
        assertTrue(
            seenQuery.get().contains("token=tok%2B1") ||
                seenQuery.get().contains("token=tok+1")
        )
        assertTrue(seenQuery.get().contains("expires=999"))
        assertEquals("tok+1", seenTokenHeader.get())
        assertEquals("999", seenExpiresHeader.get())
        assertArrayEquals(payload, seenBody.get())
    }

    @Test
    fun download_withOptionalTokenAndExpires_returnsBytes() {
        val seenQuery = AtomicReference("")
        val seenTokenHeader = AtomicReference("")
        val seenExpiresHeader = AtomicReference("")
        server.createContext("/api/media/download/xyz") { exchange ->
            seenQuery.set(exchange.requestURI.query ?: "")
            seenTokenHeader.set(exchange.requestHeaders.getFirst("X-Download-Token") ?: "")
            seenExpiresHeader.set(exchange.requestHeaders.getFirst("X-Download-Expires") ?: "")
            val response = "download-bytes".toByteArray()
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { it.write(response) }
        }

        val client = LaravelMediaApiClient(baseUrl)
        val bytes = client.download("xyz", downloadToken = "d+tok", downloadExpires = 777L)

        assertEquals("download-bytes", bytes.toString(Charsets.UTF_8))
        assertTrue(
            seenQuery.get().contains("token=d%2Btok") ||
                seenQuery.get().contains("token=d+tok")
        )
        assertTrue(seenQuery.get().contains("expires=777"))
        assertEquals("d+tok", seenTokenHeader.get())
        assertEquals("777", seenExpiresHeader.get())
    }

    @Test
    fun non2xx_throwsIllegalState() {
        server.createContext("/api/media/request-upload") { exchange ->
            val response = "boom".toByteArray()
            exchange.sendResponseHeaders(500, response.size.toLong())
            exchange.responseBody.use { it.write(response) }
        }

        val client = LaravelMediaApiClient(baseUrl)
        assertThrows(IllegalStateException::class.java) {
            client.requestUpload()
        }
    }
}

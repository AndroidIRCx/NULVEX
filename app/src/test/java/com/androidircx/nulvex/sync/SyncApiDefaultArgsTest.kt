package com.androidircx.nulvex.sync

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SyncApiDefaultArgsTest {

    private val token = SyncAuthToken(
        accessToken = "a",
        refreshToken = "r",
        expiresAtEpochMillis = 1234L,
        deviceId = "dev-1"
    )

    @Test
    fun `registerDevice default requestSecurity passes null`() = runTest {
        val api: SyncApi = CapturingSyncApi()

        api.registerDevice(profile = "real", token = token)

        assertNull((api as CapturingSyncApi).lastRegisterSecurity)
    }

    @Test
    fun `push default requestSecurity passes null`() = runTest {
        val api: SyncApi = CapturingSyncApi()
        val op = SyncEnvelope(
            opId = "op-1",
            profile = "real",
            entityType = "note",
            entityId = "n-1",
            opType = "upsert",
            baseRevision = null,
            clientTs = 1L,
            ciphertext = byteArrayOf(1)
        )

        api.push(profile = "real", token = token, operations = listOf(op))

        val impl = api as CapturingSyncApi
        assertEquals(1, impl.lastPushedCount)
        assertNull(impl.lastPushSecurity)
    }

    @Test
    fun `pull default requestSecurity passes null`() = runTest {
        val api: SyncApi = CapturingSyncApi()

        api.pull(profile = "real", token = token, cursorToken = "c1", limit = 10)

        val impl = api as CapturingSyncApi
        assertEquals(10, impl.lastPullLimit)
        assertNull(impl.lastPullSecurity)
    }

    @Test
    fun `explicit requestSecurity is forwarded`() = runTest {
        val api: SyncApi = CapturingSyncApi()
        val security = SyncRequestSecurity(
            integrityToken = "it",
            requestHash = "h",
            issuedAtEpochSeconds = 999L
        )

        api.registerDevice(profile = "real", token = token, requestSecurity = security)
        api.push(profile = "real", token = token, operations = emptyList(), requestSecurity = security)
        api.pull(profile = "real", token = token, cursorToken = null, limit = 5, requestSecurity = security)

        val impl = api as CapturingSyncApi
        assertEquals(security, impl.lastRegisterSecurity)
        assertEquals(security, impl.lastPushSecurity)
        assertEquals(security, impl.lastPullSecurity)
    }

    private class CapturingSyncApi : SyncApi {
        var lastRegisterSecurity: SyncRequestSecurity? = null
        var lastPushSecurity: SyncRequestSecurity? = null
        var lastPullSecurity: SyncRequestSecurity? = null
        var lastPullLimit: Int = -1
        var lastPushedCount: Int = 0

        override suspend fun registerDevice(
            profile: String,
            token: SyncAuthToken,
            requestSecurity: SyncRequestSecurity?
        ): Boolean {
            lastRegisterSecurity = requestSecurity
            return true
        }

        override suspend fun push(
            profile: String,
            token: SyncAuthToken,
            operations: List<SyncEnvelope>,
            requestSecurity: SyncRequestSecurity?
        ): List<SyncPushAck> {
            lastPushedCount = operations.size
            lastPushSecurity = requestSecurity
            return operations.map { SyncPushAck(it.opId, accepted = true) }
        }

        override suspend fun pull(
            profile: String,
            token: SyncAuthToken,
            cursorToken: String?,
            limit: Int,
            requestSecurity: SyncRequestSecurity?
        ): SyncPullResult {
            lastPullLimit = limit
            lastPullSecurity = requestSecurity
            return SyncPullResult(cursorToken = cursorToken, operations = emptyList())
        }
    }
}


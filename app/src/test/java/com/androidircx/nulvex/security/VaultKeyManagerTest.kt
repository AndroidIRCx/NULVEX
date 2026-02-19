package com.androidircx.nulvex.security

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VaultKeyManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockKdf: Kdf
    private lateinit var mockPrefs: VaultPreferences
    private lateinit var mockKeystoreProvider: KeystoreSecretProvider
    private lateinit var keyManager: VaultKeyManager

    private val fixedArgonSalt = ByteArray(16) { it.toByte() }
    private val fixedDbSalt = ByteArray(16) { (it + 16).toByte() }
    private val fixedKdfOutput = ByteArray(32) { it.toByte() }
    private val fixedKeystoreSecret = ByteArray(32) { (it + 100).toByte() }

    @Before
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockKdf = mockk()
        mockPrefs = mockk()
        mockKeystoreProvider = mockk()

        every { mockPrefs.getOrCreateArgonSalt() } returns fixedArgonSalt
        every { mockPrefs.getOrCreateDbSalt() } returns fixedDbSalt
        every { mockKdf.deriveKey(any(), any(), any()) } returns fixedKdfOutput
        every { mockKeystoreProvider.getOrCreateSecret(any()) } returns fixedKeystoreSecret

        keyManager = VaultKeyManager(
            context = mockContext,
            kdf = mockKdf,
            prefs = mockPrefs,
            keystoreSecretProvider = mockKeystoreProvider
        )
    }

    @Test
    fun `deriveMasterKey produces 32-byte key`() {
        val key = keyManager.deriveMasterKey("1234".toCharArray())
        assertEquals(32, key.size)
    }

    @Test
    fun `deriveMasterKey is deterministic for same inputs`() {
        val key1 = keyManager.deriveMasterKey("1234".toCharArray())
        val key2 = keyManager.deriveMasterKey("1234".toCharArray())
        assertArrayEquals(key1, key2)
    }

    @Test
    fun `deriveMasterKey changes when KDF output changes`() {
        val key1 = keyManager.deriveMasterKey("1234".toCharArray())

        every { mockKdf.deriveKey(any(), any(), any()) } returns ByteArray(32) { (it + 50).toByte() }
        val key2 = keyManager.deriveMasterKey("1234".toCharArray())

        assertFalse(key1.contentEquals(key2))
    }

    @Test
    fun `deriveMasterKey changes when keystore secret changes`() {
        val key1 = keyManager.deriveMasterKey("1234".toCharArray())

        every { mockKeystoreProvider.getOrCreateSecret(any()) } returns ByteArray(32) { 0xFF.toByte() }
        val key2 = keyManager.deriveMasterKey("1234".toCharArray())

        assertFalse(key1.contentEquals(key2))
    }

    @Test
    fun `deriveDbKey produces 32-byte key`() {
        val masterKey = ByteArray(32) { it.toByte() }
        val dbKey = keyManager.deriveDbKey(masterKey)
        assertEquals(32, dbKey.size)
    }

    @Test
    fun `deriveDbKey is deterministic for same master key`() {
        val masterKey = ByteArray(32) { it.toByte() }
        val dbKey1 = keyManager.deriveDbKey(masterKey)
        val dbKey2 = keyManager.deriveDbKey(masterKey)
        assertArrayEquals(dbKey1, dbKey2)
    }

    @Test
    fun `deriveDbKey uses db salt`() {
        val masterKey = ByteArray(32) { it.toByte() }
        val dbKey1 = keyManager.deriveDbKey(masterKey)

        every { mockPrefs.getOrCreateDbSalt() } returns ByteArray(16) { 0xFF.toByte() }
        val dbKey2 = keyManager.deriveDbKey(masterKey)

        assertFalse(dbKey1.contentEquals(dbKey2))
    }

    @Test
    fun `deriveNoteKey produces 32-byte key`() {
        val masterKey = ByteArray(32) { it.toByte() }
        val noteKey = keyManager.deriveNoteKey(masterKey)
        assertEquals(32, noteKey.size)
    }

    @Test
    fun `deriveNoteKey is deterministic for same master key`() {
        val masterKey = ByteArray(32) { it.toByte() }
        val noteKey1 = keyManager.deriveNoteKey(masterKey)
        val noteKey2 = keyManager.deriveNoteKey(masterKey)
        assertArrayEquals(noteKey1, noteKey2)
    }

    @Test
    fun `deriveDbKey and deriveNoteKey produce different keys`() {
        val masterKey = ByteArray(32) { it.toByte() }
        val dbKey = keyManager.deriveDbKey(masterKey)
        val noteKey = keyManager.deriveNoteKey(masterKey)
        assertFalse(dbKey.contentEquals(noteKey))
    }

    @Test
    fun `deriveMasterKey and deriveDbKey produce different keys`() {
        val masterKey = keyManager.deriveMasterKey("1234".toCharArray())
        val dbKey = keyManager.deriveDbKey(masterKey)
        assertFalse(masterKey.contentEquals(dbKey))
    }
}

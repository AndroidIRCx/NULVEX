package com.androidircx.nulvex.pro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedKeyStoreTest {

    private lateinit var context: Context
    private lateinit var store: SharedKeyStore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("nulvex_shared_keys", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        store = SharedKeyStore(context)
    }

    @Test
    fun generateXChaChaKey_persistsMetadataAndMaterial() {
        val key = store.generateXChaChaKey("My X Key")

        val listed = store.listKeys()
        val material = store.getKeyMaterial(key.id)

        assertEquals("My X Key", key.label)
        assertEquals("generated", key.source)
        assertEquals("xchacha20poly1305_key", key.format)
        assertTrue(listed.any { it.id == key.id })
        assertNotNull(material)
        assertEquals(32, material!!.size)
    }

    @Test
    fun generatePgpKey_persistsKey() {
        val key = store.generatePgpKey("My PGP")

        val listed = store.listKeys()
        val material = store.getKeyMaterial(key.id)

        assertEquals("generated", key.source)
        assertEquals("pgp_secret", key.format)
        assertTrue(key.fingerprint.isNotBlank())
        assertTrue(listed.any { it.id == key.id })
        assertNotNull(material)
        assertTrue(material!!.isNotEmpty())
    }

    @Test
    fun transferPayload_canBeImported() {
        val generated = store.generateXChaChaKey("Share me")
        val payload = store.buildTransferPayload(generated.id)

        val imported = store.importTransferPayload(payload!!, source = "qr")

        assertEquals("qr", imported.source)
        assertEquals(generated.format, imported.format)
        assertTrue(store.listKeys().size >= 2)
    }

    @Test
    fun buildQrTransferPayload_xchachaKey_returnsValidJson() {
        val key = store.generateXChaChaKey("QR Test Key")
        val payload = store.buildQrTransferPayload(key.id)

        assertNotNull(payload)
        val json = org.json.JSONObject(payload!!)
        assertEquals(1, json.getInt("v"))
        assertEquals("nulvex-key-share", json.getString("type"))
        assertEquals("xchacha20poly1305_key", json.getString("format"))
        assertEquals("QR Test Key", json.getString("label"))
        assertTrue(json.getString("material_b64").isNotBlank())
    }

    @Test
    fun buildQrTransferPayload_pgpKey_stripsSecretToPublicFormat() {
        val key = store.generatePgpKey("QR PGP Key")
        val payload = store.buildQrTransferPayload(key.id)

        assertNotNull(payload)
        val json = org.json.JSONObject(payload!!)
        assertEquals("pgp_public", json.getString("format"))
        assertTrue(json.getString("material_b64").isNotBlank())
    }

    @Test
    fun buildQrTransferPayload_unknownId_returnsNull() {
        val result = store.buildQrTransferPayload("nonexistent-id-xyz")
        assertEquals(null, result)
    }

    @Test
    fun exportAndImportManagerBackup_encryptedRoundTrip() {
        store.generateXChaChaKey("Before export")
        val exported = store.exportManagerBackup(encrypted = true, password = "123456".toCharArray())

        context.getSharedPreferences("nulvex_shared_keys", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()

        val freshStore = SharedKeyStore(context)
        val importedCount = freshStore.importManagerBackup(exported, "123456".toCharArray())

        assertTrue(importedCount >= 1)
        assertTrue(freshStore.listKeys().isNotEmpty())
    }
}

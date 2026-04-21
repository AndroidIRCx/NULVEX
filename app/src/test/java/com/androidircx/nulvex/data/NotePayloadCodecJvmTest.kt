package com.androidircx.nulvex.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotePayloadCodecJvmTest {

    @Test
    fun `encode and decode preserve full payload`() {
        val payload = NotePayload(
            text = "hello",
            checklist = listOf(
                ChecklistItem(id = "c1", text = "first", checked = true)
            ),
            labels = listOf("work", "infra"),
            attachments = listOf(
                NoteAttachment(
                    id = "a1",
                    name = "doc.txt",
                    mimeType = "text/plain",
                    byteCount = 42L
                )
            ),
            pinned = true,
            shareKeyId = "key-1"
        )

        val encoded = NotePayloadCodec.encode(payload)
        val decoded = NotePayloadCodec.decode(encoded)

        assertNotNull(decoded)
        assertEquals(payload.text, decoded!!.text)
        assertEquals(payload.pinned, decoded.pinned)
        assertEquals(payload.shareKeyId, decoded.shareKeyId)
        assertEquals(1, decoded.checklist.size)
        assertEquals(2, decoded.labels.size)
        assertEquals(1, decoded.attachments.size)
    }

    @Test
    fun `decode returns null for non json payload`() {
        assertNull(NotePayloadCodec.decode("not-json"))
    }

    @Test
    fun `decode returns null when text key missing`() {
        assertNull(NotePayloadCodec.decode("""{"v":1,"pinned":false}"""))
    }

    @Test
    fun `decode returns null for malformed json`() {
        assertNull(NotePayloadCodec.decode("{\"text\":"))
    }

    @Test
    fun `decode normalizes blank share key to null`() {
        val raw = """
            {
              "v": 1,
              "text": "x",
              "pinned": false,
              "share_key_id": "   "
            }
        """.trimIndent()

        val decoded = NotePayloadCodec.decode(raw)

        assertNotNull(decoded)
        assertNull(decoded!!.shareKeyId)
    }

    @Test
    fun `decode filters invalid checklist and attachment ids and blank labels`() {
        val raw = """
            {
              "v": 1,
              "text": "x",
              "pinned": true,
              "checklist": [
                {"id": "", "text": "skip", "checked": true},
                {"id": "c1", "text": "keep", "checked": false}
              ],
              "labels": ["ok", "   "],
              "attachments": [
                {"id": "", "name": "skip", "mimeType": "text/plain", "byteCount": 1},
                {"id": "a1", "name": "keep", "byteCount": 9}
              ]
            }
        """.trimIndent()

        val decoded = NotePayloadCodec.decode(raw)

        assertNotNull(decoded)
        assertEquals(1, decoded!!.checklist.size)
        assertEquals("c1", decoded.checklist.first().id)
        assertEquals(1, decoded.labels.size)
        assertEquals("ok", decoded.labels.first())
        assertEquals(1, decoded.attachments.size)
        assertEquals("a1", decoded.attachments.first().id)
        assertEquals("application/octet-stream", decoded.attachments.first().mimeType)
    }

    @Test
    fun `encode stores null share key as json null and decode keeps null`() {
        val payload = NotePayload(
            text = "x",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false,
            shareKeyId = null
        )

        val encoded = NotePayloadCodec.encode(payload)
        assertTrue(encoded.contains("\"share_key_id\":null"))

        val decoded = NotePayloadCodec.decode(encoded)
        assertNotNull(decoded)
        assertNull(decoded!!.shareKeyId)
    }
}

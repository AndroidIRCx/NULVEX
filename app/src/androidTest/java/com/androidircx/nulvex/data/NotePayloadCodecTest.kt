package com.androidircx.nulvex.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotePayloadCodecTest {

    @Test
    fun encodeAndDecodeRoundTripWithFullPayload() {
        val payload = NotePayload(
            text = "Test note content",
            checklist = listOf(
                ChecklistItem(id = "item1", text = "Task 1", checked = true),
                ChecklistItem(id = "item2", text = "Task 2", checked = false)
            ),
            labels = listOf("work", "important"),
            attachments = listOf(
                NoteAttachment(id = "att1", name = "image.png", mimeType = "image/png", byteCount = 1024)
            ),
            pinned = true
        )

        val encoded = NotePayloadCodec.encode(payload)
        val decoded = NotePayloadCodec.decode(encoded)

        assertNotNull(decoded)
        assertEquals(payload.text, decoded!!.text)
        assertEquals(payload.pinned, decoded.pinned)
        assertEquals(payload.checklist.size, decoded.checklist.size)
        assertEquals(payload.labels, decoded.labels)
        assertEquals(payload.attachments.size, decoded.attachments.size)
    }

    @Test
    fun encodeAndDecodeEmptyPayload() {
        val payload = NotePayload(
            text = "",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )

        val encoded = NotePayloadCodec.encode(payload)
        val decoded = NotePayloadCodec.decode(encoded)

        assertNotNull(decoded)
        assertEquals("", decoded!!.text)
        assertTrue(decoded.checklist.isEmpty())
        assertTrue(decoded.labels.isEmpty())
        assertTrue(decoded.attachments.isEmpty())
        assertFalse(decoded.pinned)
    }

    @Test
    fun decodeInvalidJsonReturnsNull() {
        val result = NotePayloadCodec.decode("not valid json")
        assertNull(result)
    }

    @Test
    fun decodeEmptyStringReturnsNull() {
        val result = NotePayloadCodec.decode("")
        assertNull(result)
    }

    @Test
    fun decodeJsonWithoutTextFieldReturnsNull() {
        val json = """{"pinned": true}"""
        val result = NotePayloadCodec.decode(json)
        assertNull(result)
    }

    @Test
    fun encodeProducesValidJson() {
        val payload = NotePayload(
            text = "Hello",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )

        val encoded = NotePayloadCodec.encode(payload)

        assertTrue(encoded.startsWith("{"))
        assertTrue(encoded.endsWith("}"))
        assertTrue(encoded.contains("\"text\""))
        assertTrue(encoded.contains("\"v\""))
    }

    @Test
    fun checklistItemsPreserveAllFields() {
        val checklist = listOf(
            ChecklistItem(id = "uuid-123", text = "Buy milk", checked = true),
            ChecklistItem(id = "uuid-456", text = "Call mom", checked = false)
        )
        val payload = NotePayload(
            text = "",
            checklist = checklist,
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )

        val decoded = NotePayloadCodec.decode(NotePayloadCodec.encode(payload))!!

        assertEquals(2, decoded.checklist.size)
        assertEquals("uuid-123", decoded.checklist[0].id)
        assertEquals("Buy milk", decoded.checklist[0].text)
        assertTrue(decoded.checklist[0].checked)
        assertEquals("uuid-456", decoded.checklist[1].id)
        assertEquals("Call mom", decoded.checklist[1].text)
        assertFalse(decoded.checklist[1].checked)
    }

    @Test
    fun attachmentsPreserveAllFields() {
        val attachments = listOf(
            NoteAttachment(
                id = "att-001",
                name = "photo.jpg",
                mimeType = "image/jpeg",
                byteCount = 2048576
            )
        )
        val payload = NotePayload(
            text = "",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = attachments,
            pinned = false
        )

        val decoded = NotePayloadCodec.decode(NotePayloadCodec.encode(payload))!!

        assertEquals(1, decoded.attachments.size)
        assertEquals("att-001", decoded.attachments[0].id)
        assertEquals("photo.jpg", decoded.attachments[0].name)
        assertEquals("image/jpeg", decoded.attachments[0].mimeType)
        assertEquals(2048576L, decoded.attachments[0].byteCount)
    }

    @Test
    fun unicodeTextIsPreserved() {
        val payload = NotePayload(
            text = "Привет мир! 你好世界!",
            checklist = listOf(
                ChecklistItem(id = "1", text = "日本語テスト", checked = false)
            ),
            labels = listOf("한국어", "Ελληνικά"),
            attachments = emptyList(),
            pinned = true
        )

        val decoded = NotePayloadCodec.decode(NotePayloadCodec.encode(payload))!!

        assertEquals(payload.text, decoded.text)
        assertEquals("日本語テスト", decoded.checklist[0].text)
        assertEquals(listOf("한국어", "Ελληνικά"), decoded.labels)
    }

    @Test
    fun specialCharactersInTextAreEscapedProperly() {
        val payload = NotePayload(
            text = "Line 1\nLine 2\tTabbed\r\nWindows line",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )

        val encoded = NotePayloadCodec.encode(payload)
        val decoded = NotePayloadCodec.decode(encoded)!!

        assertEquals(payload.text, decoded.text)
    }

    @Test
    fun quotesInTextAreEscapedProperly() {
        val payload = NotePayload(
            text = """He said "Hello" and 'World'""",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )

        val encoded = NotePayloadCodec.encode(payload)
        val decoded = NotePayloadCodec.decode(encoded)!!

        assertEquals(payload.text, decoded.text)
    }

    @Test
    fun blankLabelsAreFilteredOutDuringDecode() {
        val json = """{"v":1,"text":"test","checklist":[],"labels":["valid","  ",""],"attachments":[],"pinned":false}"""

        val decoded = NotePayloadCodec.decode(json)!!

        assertEquals(1, decoded.labels.size)
        assertEquals("valid", decoded.labels[0])
    }

    @Test
    fun missingOptionalFieldsUseDefaults() {
        val json = """{"text":"minimal"}"""

        val decoded = NotePayloadCodec.decode(json)!!

        assertEquals("minimal", decoded.text)
        assertFalse(decoded.pinned)
        assertTrue(decoded.checklist.isEmpty())
        assertTrue(decoded.labels.isEmpty())
        assertTrue(decoded.attachments.isEmpty())
    }
}

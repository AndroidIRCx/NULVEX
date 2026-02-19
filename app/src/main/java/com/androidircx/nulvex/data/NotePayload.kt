package com.androidircx.nulvex.data

import org.json.JSONArray
import org.json.JSONObject

data class NotePayload(
    val text: String,
    val checklist: List<ChecklistItem>,
    val labels: List<String>,
    val attachments: List<NoteAttachment>,
    val pinned: Boolean
)

object NotePayloadCodec {
    private const val KEY_VERSION = "v"
    private const val KEY_TEXT = "text"
    private const val KEY_CHECKLIST = "checklist"
    private const val KEY_LABELS = "labels"
    private const val KEY_ATTACHMENTS = "attachments"
    private const val KEY_PINNED = "pinned"
    private const val VERSION = 1

    fun encode(payload: NotePayload): String {
        val root = JSONObject()
        root.put(KEY_VERSION, VERSION)
        root.put(KEY_TEXT, payload.text)
        root.put(KEY_PINNED, payload.pinned)
        val checklistArray = JSONArray()
        payload.checklist.forEach { item ->
            val itemObj = JSONObject()
            itemObj.put("id", item.id)
            itemObj.put("text", item.text)
            itemObj.put("checked", item.checked)
            checklistArray.put(itemObj)
        }
        root.put(KEY_CHECKLIST, checklistArray)
        val labelArray = JSONArray()
        payload.labels.forEach { label ->
            labelArray.put(label)
        }
        root.put(KEY_LABELS, labelArray)
        val attachmentArray = JSONArray()
        payload.attachments.forEach { attachment ->
            val att = JSONObject()
            att.put("id", attachment.id)
            att.put("name", attachment.name)
            att.put("mimeType", attachment.mimeType)
            att.put("byteCount", attachment.byteCount)
            attachmentArray.put(att)
        }
        root.put(KEY_ATTACHMENTS, attachmentArray)
        return root.toString()
    }

    fun decode(raw: String): NotePayload? {
        val trimmed = raw.trim()
        if (!trimmed.startsWith("{")) return null
        return try {
            val root = JSONObject(trimmed)
            if (!root.has(KEY_TEXT)) return null
            val text = root.optString(KEY_TEXT, "")
            val pinned = root.optBoolean(KEY_PINNED, false)
            val checklist = mutableListOf<ChecklistItem>()
            val checklistArray = root.optJSONArray(KEY_CHECKLIST) ?: JSONArray()
            for (i in 0 until checklistArray.length()) {
                val item = checklistArray.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                val itemText = item.optString("text", "")
                val checked = item.optBoolean("checked", false)
                if (id.isNotBlank()) {
                    checklist.add(ChecklistItem(id = id, text = itemText, checked = checked))
                }
            }
            val labels = mutableListOf<String>()
            val labelArray = root.optJSONArray(KEY_LABELS) ?: JSONArray()
            for (i in 0 until labelArray.length()) {
                val label = labelArray.optString(i, "").trim()
                if (label.isNotBlank()) labels.add(label)
            }
            val attachments = mutableListOf<NoteAttachment>()
            val attachmentArray = root.optJSONArray(KEY_ATTACHMENTS) ?: JSONArray()
            for (i in 0 until attachmentArray.length()) {
                val item = attachmentArray.optJSONObject(i) ?: continue
                val id = item.optString("id", "")
                val name = item.optString("name", "")
                val mimeType = item.optString("mimeType", "application/octet-stream")
                val byteCount = item.optLong("byteCount", 0L)
                if (id.isNotBlank()) {
                    attachments.add(
                        NoteAttachment(
                            id = id,
                            name = name,
                            mimeType = mimeType,
                            byteCount = byteCount
                        )
                    )
                }
            }
            NotePayload(
                text = text,
                checklist = checklist,
                labels = labels,
                attachments = attachments,
                pinned = pinned
            )
        } catch (_: Exception) {
            null
        }
    }
}

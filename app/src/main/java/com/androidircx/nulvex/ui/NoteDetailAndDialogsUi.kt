package com.androidircx.nulvex.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Clear
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.viewinterop.AndroidView
import com.androidircx.nulvex.ads.AdManager
import com.androidircx.nulvex.BuildConfig
import com.androidircx.nulvex.data.ChecklistItem
import com.androidircx.nulvex.data.Note
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import com.androidircx.nulvex.i18n.tx
import com.androidircx.nulvex.R
import com.androidircx.nulvex.security.SecurityEventStore
import com.androidircx.nulvex.ui.theme.Brass
import com.androidircx.nulvex.ui.theme.Coal
import com.androidircx.nulvex.ui.theme.Ember
import com.androidircx.nulvex.ui.theme.Ink
import com.androidircx.nulvex.ui.theme.Moss
import com.androidircx.nulvex.ui.theme.Sand
import com.androidircx.nulvex.ui.theme.ThemeMode
import com.androidircx.nulvex.ui.theme.ThemePalette
import com.androidircx.nulvex.ui.theme.ThemeColors
import com.androidircx.nulvex.ui.theme.BuiltInThemes
import kotlin.math.max
import android.net.Uri
import android.content.Intent
import android.content.ClipData
import android.provider.OpenableColumns
import android.graphics.Bitmap
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.speech.RecognizerIntent
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
internal fun NoteDetailScreen(
    state: UiState,
    onClose: () -> Unit,
    onOpenLinkedNote: (String) -> Unit,
    onUpdateNoteText: (String, String, Long?) -> Unit,
    onSaveEditedNote: (String, String, String, List<String>, List<android.net.Uri>, Long?) -> Unit,
    onShareNote: (String) -> Unit,
    onExportNoteFile: (String) -> Unit,
    onDelete: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleArchived: (String) -> Unit,
    onRestoreNoteFromTrash: (String) -> Unit,
    onSetNoteReminder: (String, Long) -> Unit,
    onSetNoteReminderRepeat: (String, String?) -> Unit,
    onClearNoteReminder: (String) -> Unit,
    onRestoreNoteRevision: (String, String) -> Unit,
    onToggleChecklistItem: (String, String) -> Unit,
    onAddChecklistItem: (String, String) -> Unit,
    onRemoveChecklistItem: (String, String) -> Unit,
    onUpdateChecklistText: (String, String, String) -> Unit,
    onMoveChecklistItem: (String, String, Int) -> Unit,
    onLoadAttachmentPreview: (String, String) -> Unit,
    onRemoveAttachment: (String, String) -> Unit,
    onExportAttachment: (String, String, String) -> Unit,
    onNoteEditDraftChanged: (String, String, Long?) -> Unit = { _, _, _ -> },
    onClearNoteEditDraft: () -> Unit = {},
    onUndoNoteEdit: (String) -> Unit = {},
    onRedoNoteEdit: (String) -> Unit = {}
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val note = state.selectedNote
    if (note == null) {
        Text(tx("Note unavailable"), color = onSurface)
        return
    }
    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember(note.id) { mutableStateOf(note.title) }
    var editText by remember(note.id) { mutableStateOf(note.text) }
    var editLabels by remember(note.id) { mutableStateOf(note.labels) }
    var newLabel by remember { mutableStateOf("") }
    var newAttachments by remember { mutableStateOf(listOf<Uri>()) }
    var expiryChoice by remember(note.id) {
        mutableStateOf(if (note.expiresAt == null) "none" else "custom")
    }
    var customExpiresAt by remember(note.id) {
        mutableStateOf(note.expiresAt)
    }
    var checklistInput by remember { mutableStateOf("") }
    var showMarkdownPreview by remember(note.id) { mutableStateOf(false) }
    var showRevisionHistory by remember(note.id) { mutableStateOf(false) }
    var previewAttachmentId by remember(note.id) { mutableStateOf<String?>(null) }
    var editingChecklistId by remember { mutableStateOf<String?>(null) }
    var editingChecklistText by remember { mutableStateOf("") }
    val checklistBounds = remember { mutableStateMapOf<String, IntRange>() }
    var draggingChecklistId by remember { mutableStateOf<String?>(null) }
    var dragY by remember { mutableStateOf(0f) }
    var lastSwapTargetId by remember { mutableStateOf<String?>(null) }
    var dragTargetId by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val editContentRequester = remember { BringIntoViewRequester() }
    val checklistSectionRequester = remember { BringIntoViewRequester() }
    val editingChecklistItemRequester = remember { BringIntoViewRequester() }
    val checklistInputRequester = remember { BringIntoViewRequester() }
    val checklistInputFocusRequester = remember { FocusRequester() }
    val editChecklistTextFocusRequester = remember { FocusRequester() }
    var lastChecklistCount by remember(note.id) { mutableIntStateOf(note.checklist.size) }
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val dateTimeFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) newAttachments = newAttachments + uri
    }

    fun openDateTimePicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        customExpiresAt = cal.timeInMillis
                        expiryChoice = "custom"
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
                ).show()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun openReminderPicker() {
        val now = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, day)
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        onSetNoteReminder(note.id, cal.timeInMillis)
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
                ).show()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val editedExpiresAt = when (expiryChoice) {
        "none" -> null
        "1h" -> System.currentTimeMillis() + 3_600_000L
        "24h" -> System.currentTimeMillis() + 86_400_000L
        "7d" -> System.currentTimeMillis() + 604_800_000L
        else -> customExpiresAt
    }

    LaunchedEffect(isEditing, editText, expiryChoice, customExpiresAt, note.id) {
        if (isEditing) {
            onNoteEditDraftChanged(note.id, editText, editedExpiresAt)
        }
    }
    LaunchedEffect(state.pendingNoteEdit, isEditing, note.id) {
        if (!isEditing) return@LaunchedEffect
        val pending = state.pendingNoteEdit ?: return@LaunchedEffect
        if (pending.noteId != note.id) return@LaunchedEffect
        editText = pending.text
        customExpiresAt = pending.expiresAt
        expiryChoice = if (pending.expiresAt == null) "none" else "custom"
    }
    LaunchedEffect(isEditing) {
        if (isEditing) {
            delay(180)
            editContentRequester.bringIntoView()
        }
    }
    LaunchedEffect(editingChecklistId) {
        if (editingChecklistId != null) {
            delay(180)
            editingChecklistItemRequester.bringIntoView()
            editChecklistTextFocusRequester.requestFocus()
        }
    }
    LaunchedEffect(note.checklist.size) {
        if (note.checklist.size > lastChecklistCount) {
            delay(180)
            checklistInputRequester.bringIntoView()
        }
        lastChecklistCount = note.checklist.size
    }

    fun cancelEditing() {
        isEditing = false
        editText = note.text
        editLabels = note.labels
        newLabel = ""
        newAttachments = emptyList()
        customExpiresAt = note.expiresAt
        expiryChoice = if (note.expiresAt == null) "none" else "custom"
        showMarkdownPreview = false
        editingChecklistId = null
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onClearNoteEditDraft()
    }

    fun saveEditing() {
        onSaveEditedNote(note.id, editTitle, editText, editLabels, newAttachments, editedExpiresAt)
        isEditing = false
        editingChecklistId = null
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
    }

    val previewAttachment = note.attachments.firstOrNull { it.id == previewAttachmentId }
    val previewBitmap = previewAttachment?.let { state.attachmentPreviews[it.id] }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Box(modifier = Modifier.padding(20.dp).imePadding()) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(top = if (isEditing) 48.dp else 0.dp)
            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditing) {
                    // Editable title (Google Keep style), same as the create screen.
                    BasicTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleLarge.copy(color = onSurface),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (editTitle.isEmpty()) {
                                Text(
                                    tx("Title"),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = onSurface.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (editTitle.isNotEmpty()) {
                        IconButton(onClick = { editTitle = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = tx("Clear title"), tint = onSurface.copy(alpha = 0.7f))
                        }
                    }
                } else {
                    Text(
                        text = note.title.ifBlank { tx("Note") },
                        style = MaterialTheme.typography.titleLarge,
                        color = onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                IconButton(onClick = {
                    if (note.trashedAt != null) return@IconButton
                    if (isEditing) {
                        cancelEditing()
                    } else {
                        isEditing = true
                        showMarkdownPreview = false
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = if (isEditing) stringResource(R.string.notes_cd_cancel_edit) else stringResource(R.string.notes_cd_edit_note),
                        tint = if (note.trashedAt != null) onSurface.copy(alpha = 0.3f) else if (isEditing) Brass else onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = { onTogglePinned(note.id) }) {
                    Icon(
                        imageVector = if (note.pinned) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (note.pinned) stringResource(R.string.notes_cd_unpin) else stringResource(R.string.notes_cd_pin),
                        tint = if (note.pinned) Brass else onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                if (note.trashedAt == null) {
                    IconButton(onClick = { onToggleArchived(note.id) }) {
                        Icon(
                            imageVector = if (note.archivedAt == null) Icons.Filled.Archive else Icons.Filled.Unarchive,
                            contentDescription = if (note.archivedAt == null) stringResource(R.string.notes_cd_archive) else stringResource(R.string.notes_cd_unarchive),
                            tint = Brass
                        )
                    }
                } else {
                    IconButton(onClick = { onRestoreNoteFromTrash(note.id) }) {
                        Icon(Icons.Filled.RestoreFromTrash, contentDescription = stringResource(R.string.notes_cd_restore), tint = Brass)
                    }
                }
                IconButton(onClick = { onShareNote(note.id) }) {
                    Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.notes_cd_share_link), tint = Moss)
                }
                IconButton(onClick = { onExportNoteFile(note.id) }) {
                    Icon(Icons.Filled.FileDownload, contentDescription = stringResource(R.string.notes_cd_export_file), tint = Moss)
                }
                IconButton(onClick = { showRevisionHistory = true }) {
                    Icon(Icons.Filled.History, contentDescription = stringResource(R.string.notes_cd_history), tint = Moss)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (note.trashedAt == null) {
                Text(
                    text = stringResource(R.string.notes_section_metadata),
                    style = MaterialTheme.typography.labelLarge,
                    color = onSurface.copy(alpha = 0.75f),
                    modifier = Modifier.testTag("note_section_metadata")
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { openReminderPicker() },
                        modifier = Modifier.background(Moss.copy(alpha = 0.16f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = stringResource(R.string.notes_cd_set_reminder), tint = Moss)
                    }
                    if (note.reminderAt != null) {
                        IconButton(onClick = { onClearNoteReminder(note.id) }) {
                            Icon(Icons.Filled.NotificationsOff, contentDescription = stringResource(R.string.notes_cd_clear_reminder), tint = Ember)
                        }
                    }
                }
                if (note.reminderAt != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val currentRepeat = note.reminderRepeat
                        Chip(stringResource(R.string.notes_repeat_none), currentRepeat == null) { onSetNoteReminderRepeat(note.id, null) }
                        Chip(stringResource(R.string.notes_repeat_daily), currentRepeat == "daily") { onSetNoteReminderRepeat(note.id, "daily") }
                        Chip(stringResource(R.string.notes_repeat_weekly), currentRepeat == "weekly") { onSetNoteReminderRepeat(note.id, "weekly") }
                        Chip(stringResource(R.string.notes_repeat_monthly), currentRepeat == "monthly") { onSetNoteReminderRepeat(note.id, "monthly") }
                    }
                }
                if (note.reminderAt != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val reminderText = tx("Reminder: {value}")
                        .replace("{value}", dateTimeFormatter.format(java.util.Date(note.reminderAt)))
                    Text(
                        reminderText + (note.reminderRepeat?.let { " (" + it.uppercase() + ")" } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            if (state.noteLinkedNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(tx("Linked notes"), style = MaterialTheme.typography.labelLarge, color = onSurface.copy(alpha = 0.75f))
                Spacer(modifier = Modifier.height(6.dp))
                state.noteLinkedNotes.forEach { linked ->
                    val title = linked.text.lineSequence().firstOrNull { it.trim().isNotBlank() }?.trim().orEmpty()
                    TextButton(onClick = { onOpenLinkedNote(linked.id) }) {
                        Text(text = if (title.isNotBlank()) title else linked.id, color = Moss)
                    }
                }
            }
            if (state.noteBacklinks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(tx("Backlinks"), style = MaterialTheme.typography.labelLarge, color = onSurface.copy(alpha = 0.75f))
                Spacer(modifier = Modifier.height(6.dp))
                state.noteBacklinks.forEach { linked ->
                    val title = linked.text.lineSequence().firstOrNull { it.trim().isNotBlank() }?.trim().orEmpty()
                    TextButton(onClick = { onOpenLinkedNote(linked.id) }) {
                        Text(text = if (title.isNotBlank()) title else linked.id, color = Brass)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.notes_section_content),
                style = MaterialTheme.typography.labelLarge,
                color = onSurface.copy(alpha = 0.75f),
                modifier = Modifier.testTag("note_section_content")
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (isEditing) {
                if (showMarkdownPreview) {
                    val preview = remember(editText) { MarkdownPreviewRenderer.render(editText) }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = onSurface.copy(alpha = 0.06f)
                    ) {
                        Text(
                            text = preview.ifBlank { tx("Nothing to preview") },
                            color = onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = {
                            editText = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .bringIntoViewRequester(editContentRequester)
                            .bringIntoViewOnFocus(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default,
                            autoCorrectEnabled = false
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(tx("Expiry"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    Chip(tx("None"), expiryChoice == "none") { expiryChoice = "none" }
                    Chip(tx("1h"), expiryChoice == "1h") { expiryChoice = "1h" }
                    Chip(tx("24h"), expiryChoice == "24h") { expiryChoice = "24h" }
                    Chip(tx("7d"), expiryChoice == "7d") { expiryChoice = "7d" }
                    Chip(tx("Custom"), expiryChoice == "custom") { openDateTimePicker() }
                }
                if (customExpiresAt != null && expiryChoice == "custom") {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        tx("Selected expiry: {value}")
                            .replace("{value}", dateTimeFormatter.format(java.util.Date(customExpiresAt!!))),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(tx("Labels"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        label = { Text(tx("Add label")) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val trimmed = newLabel.trim()
                            if (trimmed.isNotBlank() && !editLabels.contains(trimmed)) {
                                editLabels = editLabels + trimmed
                                newLabel = ""
                            }
                        },
                        enabled = newLabel.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text(tx("ADD"))
                    }
                }
                val labelSuggestions = if (newLabel.isNotBlank()) {
                    state.savedLabels.filter {
                        it.contains(newLabel.trim(), ignoreCase = true) && !editLabels.contains(it)
                    }
                } else emptyList()
                if (labelSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        labelSuggestions.forEach { suggestion ->
                            Box(
                                modifier = Modifier
                                    .background(Moss.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        editLabels = editLabels + suggestion
                                        newLabel = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(suggestion, style = MaterialTheme.typography.labelMedium, color = Moss)
                            }
                        }
                    }
                }
                if (editLabels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        editLabels.forEach { label ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Moss.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)
                            ) {
                                Text(label, color = Moss, style = MaterialTheme.typography.labelLarge)
                                IconButton(
                                    onClick = { editLabels = editLabels.filterNot { it == label } },
                                    modifier = Modifier.height(24.dp).width(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = tx("Remove"),
                                        tint = Ember,
                                        modifier = Modifier.height(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(tx("Add images"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { imagePicker.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text(tx("ADD IMAGE"))
                    }
                    if (newAttachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            tx("{count} attached").replace("{count}", newAttachments.size.toString()),
                            color = onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                if (newAttachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    newAttachments.forEach { uri ->
                        val name = resolveDisplayName(context, uri) ?: tx("Image")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(name, color = onSurface, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                newAttachments = newAttachments.filterNot { it == uri }
                            }) {
                                Text(tx("REMOVE"), color = Ember)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else if (note.text.isNotBlank()) {
                Text(note.text, color = onSurface, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                tx("Checklist"),
                color = onSurface.copy(alpha = 0.75f),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .testTag("note_section_checklist")
                    .bringIntoViewRequester(checklistSectionRequester)
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (note.checklist.isNotEmpty()) {
                note.checklist.forEach { item ->
                    val isDragging = draggingChecklistId == item.id
                    val isDragTarget = dragTargetId == item.id && !isDragging
                    val scale by animateFloatAsState(
                        targetValue = if (isDragging) 1.02f else 1f,
                        animationSpec = spring(),
                        label = "checklistScale"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (isDragging) 0.95f else 1f,
                        animationSpec = spring(),
                        label = "checklistAlpha"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (editingChecklistId == item.id) {
                                    Modifier.bringIntoViewRequester(editingChecklistItemRequester)
                                } else {
                                    Modifier
                                }
                            )
                            .background(
                                if (isDragging) {
                                    Brass.copy(alpha = 0.12f)
                                } else if (isDragTarget) {
                                    Moss.copy(alpha = 0.15f)
                                } else {
                                    Color.Transparent
                                },
                                RoundedCornerShape(10.dp)
                            )
                            .scale(scale)
                            .alpha(alpha)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .onGloballyPositioned { coords ->
                                val top = coords.positionInParent().y.toInt()
                                val bottom = top + coords.size.height
                                checklistBounds[item.id] = top..bottom
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DragHandle,
                            contentDescription = tx("Hold to reorder"),
                            tint = if (isDragging) Brass else onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .padding(start = 2.dp, end = 6.dp)
                                .pointerInput(note.checklist, item.id) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { offset ->
                                            draggingChecklistId = item.id
                                            val bounds = checklistBounds[item.id]
                                            dragY = (bounds?.first ?: 0) + offset.y
                                            lastSwapTargetId = null
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDragEnd = {
                                            draggingChecklistId = null
                                            lastSwapTargetId = null
                                            dragTargetId = null
                                        },
                                        onDragCancel = {
                                            draggingChecklistId = null
                                            lastSwapTargetId = null
                                            dragTargetId = null
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragY += dragAmount.y
                                            val targetId = checklistBounds.entries.firstOrNull { (_, range) ->
                                                dragY.toInt() in range
                                            }?.key
                                            dragTargetId = targetId
                                            val sourceId = draggingChecklistId
                                            if (sourceId != null && targetId != null && targetId != sourceId &&
                                                targetId != lastSwapTargetId
                                            ) {
                                                val sourceIndex = note.checklist.indexOfFirst { it.id == sourceId }
                                                val targetIndex = note.checklist.indexOfFirst { it.id == targetId }
                                                if (sourceIndex >= 0 && targetIndex >= 0) {
                                                    val direction = if (targetIndex > sourceIndex) 1 else -1
                                                    onMoveChecklistItem(note.id, sourceId, direction)
                                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    lastSwapTargetId = targetId
                                                }
                                            }
                                        }
                                    )
                                }
                        )
                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = { onToggleChecklistItem(note.id, item.id) }
                        )
                        if (editingChecklistId == item.id) {
                            OutlinedTextField(
                                value = editingChecklistText,
                                onValueChange = { editingChecklistText = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(editChecklistTextFocusRequester)
                                    .bringIntoViewOnFocus(),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                onUpdateChecklistText(note.id, item.id, editingChecklistText)
                                editingChecklistId = null
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = tx("Save item"),
                                    tint = Brass
                                )
                            }
                            IconButton(onClick = {
                                editingChecklistId = null
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = tx("Cancel edit"),
                                    tint = Sand
                                )
                            }
                        } else {
                            Text(item.text, color = onSurface, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                editingChecklistId = item.id
                                editingChecklistText = item.text
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = tx("Edit item"),
                                    tint = Brass
                                )
                            }
                        }
                        IconButton(onClick = { onRemoveChecklistItem(note.id, item.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = tx("Remove item"),
                                tint = Ember
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = checklistInput,
                    onValueChange = { checklistInput = it },
                    label = { Text(tx("Add item")) },
                    modifier = Modifier
                        .weight(1f)
                        .bringIntoViewRequester(checklistInputRequester)
                        .focusRequester(checklistInputFocusRequester)
                        .bringIntoViewOnFocus(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onAddChecklistItem(note.id, checklistInput)
                        checklistInput = ""
                        keyboardController?.show()
                        scope.launch { checklistInputRequester.bringIntoView() }
                        checklistInputFocusRequester.requestFocus()
                    },
                    enabled = checklistInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Text(tx("ADD"))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (note.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(tx("Attachments"), color = onSurface.copy(alpha = 0.75f), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
                note.attachments.forEach { attachment ->
                    val preview = state.attachmentPreviews[attachment.id]
                    if (preview == null) {
                        androidx.compose.runtime.LaunchedEffect(attachment.id) {
                            onLoadAttachmentPreview(note.id, attachment.id)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(attachment.name, color = onSurface, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            onLoadAttachmentPreview(note.id, attachment.id)
                            previewAttachmentId = attachment.id
                        }) {
                            Text(tx("OPEN"), color = Moss)
                        }
                        TextButton(onClick = { onExportAttachment(note.id, attachment.id, attachment.name) }) {
                            Text(tx("SAVE"), color = Brass)
                        }
                        TextButton(onClick = { onRemoveAttachment(note.id, attachment.id) }) {
                            Text(tx("REMOVE"), color = Ember)
                        }
                    }
                    if (preview != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Image(
                            bitmap = preview.asImageBitmap(),
                            contentDescription = attachment.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .clickable { previewAttachmentId = attachment.id }
                                .background(
                                    onSurface.copy(alpha = 0.05f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            if (previewAttachment != null && previewBitmap != null) {
                Dialog(onDismissRequest = { previewAttachmentId = null }) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.88f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = previewAttachment.name,
                                    color = onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { previewAttachmentId = null }) {
                                    Icon(Icons.Filled.Close, contentDescription = tx("Close"), tint = onSurface)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Image(
                                bitmap = previewBitmap.asImageBitmap(),
                                contentDescription = previewAttachment.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(onSurface.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        onExportAttachment(note.id, previewAttachment.id, previewAttachment.name)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                                ) {
                                    Text(tx("SAVE"))
                                }
                                TextButton(onClick = { previewAttachmentId = null }) {
                                    Text(tx("CLOSE"), color = onSurface.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isEditing) {
                    Button(
                        onClick = { saveEditing() },
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text(tx("SAVE"))
                    }
                    TextButton(onClick = { cancelEditing() }) {
                        Text(tx("CANCEL"), color = onSurface.copy(alpha = 0.7f))
                    }
                } else {
                    Button(
                        onClick = { onDelete(note.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Ember, contentColor = Sand)
                    ) {
                        Icon(
                            imageVector = if (note.trashedAt == null) Icons.Filled.Delete else Icons.Filled.DeleteForever,
                            contentDescription = if (note.trashedAt == null) stringResource(R.string.notes_cd_delete) else stringResource(R.string.notes_cd_delete_now)
                        )
                    }
                    TextButton(onClick = onClose) {
                        Text(tx("BACK"), color = Brass)
                    }
                }
            }
        }
        if (isEditing) {
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = onSurface.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onUndoNoteEdit(note.id) },
                        enabled = state.canUndoNoteEdit
                    ) {
                        Text(tx("UNDO"), color = if (state.canUndoNoteEdit) Brass else onSurface.copy(alpha = 0.4f))
                    }
                    TextButton(
                        onClick = { onRedoNoteEdit(note.id) },
                        enabled = state.canRedoNoteEdit
                    ) {
                        Text(tx("REDO"), color = if (state.canRedoNoteEdit) Brass else onSurface.copy(alpha = 0.4f))
                    }
                    TextButton(onClick = { showMarkdownPreview = !showMarkdownPreview }) {
                        Text(if (showMarkdownPreview) tx("EDIT") else tx("PREVIEW"), color = Moss)
                    }
                    TextButton(onClick = { saveEditing() }) {
                        Text(tx("SAVE"), color = Brass)
                    }
                }
            }
        }
    }
    if (showRevisionHistory) {
        AlertDialog(
            onDismissRequest = { showRevisionHistory = false },
            title = { Text(tx("Version history")) },
            text = {
                if (state.noteRevisions.isEmpty()) {
                    Text(tx("No revisions yet"))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.noteRevisions.forEach { revision ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = dateTimeFormatter.format(java.util.Date(revision.createdAt)),
                                    modifier = Modifier.weight(1f),
                                    color = onSurface
                                )
                                TextButton(onClick = {
                                    onRestoreNoteRevision(note.id, revision.id)
                                    showRevisionHistory = false
                                }, modifier = Modifier.testTag("history_restore_${revision.id}")) {
                                    Text(tx("RESTORE"), color = Brass)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showRevisionHistory = false },
                    modifier = Modifier.testTag("history_close")
                ) {
                    Text(tx("CLOSE"))
                }
            }
        )
    }
}
}


@Composable
internal fun PendingImportDialog(
    state: UiState,
    onImportFile: (ByteArray, String, String, Boolean) -> Unit,
    onImportKeyManager: (ByteArray, String?) -> Unit,
    onImportRemote: (String, String, Boolean) -> Unit,
    onImportRemoteKeyManager: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    val import = state.pendingImport ?: return
    val onSurface = MaterialTheme.colorScheme.onSurface
    var selectedKeyId by remember { mutableStateOf(state.sharedKeys.firstOrNull()?.id ?: "") }
    var mergeMode by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var showKeyMenu by remember { mutableStateOf(false) }
    val selectedKeyLabel = state.sharedKeys.firstOrNull { it.id == selectedKeyId }?.label
        ?: state.sharedKeys.firstOrNull()?.label
        ?: tx("No keys available")

    val localImport = import as? PendingImport.LocalFile
    val remoteImport = import as? PendingImport.RemoteMedia
    val isKeysFile = localImport?.mimeType == com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME
    val isRemoteKeysFile = remoteImport?.mime == com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME
    val isNoteShare = localImport?.mimeType == com.androidircx.nulvex.pro.NulvexFileTypes.NOTE_SHARE_MIME
    val noKeys = state.sharedKeys.isEmpty() && !isKeysFile && !isRemoteKeysFile

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when {
                    isKeysFile || isRemoteKeysFile -> tx("Import Keys")
                    isNoteShare -> tx("Import Note")
                    remoteImport != null -> tx("Import from Link")
                    else -> tx("Import Backup")
                },
                color = onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (remoteImport != null) {
                    Text(
                        tx("Media ID: {id}").replace("{id}", remoteImport.mediaId.take(16) + "…"),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                }
                if (isKeysFile || isRemoteKeysFile) {
                    Text(tx("Enter password if the file was exported with encryption. Leave blank for unencrypted exports."), color = onSurface)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(tx("Password (optional)")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                } else if (noKeys) {
                    Text(tx("No keys available. Go to Settings → Keys Manager to import a shared key first."), color = Ember)
                } else {
                    Text(tx("Select the shared key used to encrypt this file:"), color = onSurface)
                    Box {
                        Button(
                            onClick = { showKeyMenu = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Coal, contentColor = Sand),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedKeyLabel, maxLines = 1)
                        }
                        DropdownMenu(expanded = showKeyMenu, onDismissRequest = { showKeyMenu = false }) {
                            state.sharedKeys.forEach { key ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(key.label, modifier = Modifier.weight(1f))
                                            if (selectedKeyId == key.id) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = Brass
                                                )
                                            }
                                        }
                                    },
                                    onClick = { selectedKeyId = key.id; showKeyMenu = false }
                                )
                            }
                        }
                    }
                    if (!isNoteShare) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = mergeMode, onCheckedChange = { mergeMode = it })
                            Text(tx("Merge with existing notes"), color = onSurface)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isKeysFile) {
                        onImportKeyManager(localImport.bytes, password.ifBlank { null })
                    } else if (isRemoteKeysFile) {
                        onImportRemoteKeyManager(remoteImport.mediaId, password.ifBlank { null })
                    } else if (localImport != null) {
                        onImportFile(localImport.bytes, localImport.mimeType, selectedKeyId, mergeMode || isNoteShare)
                    } else if (remoteImport != null) {
                        onImportRemote(remoteImport.mediaId, selectedKeyId, mergeMode)
                    } else {
                        onDismiss()
                    }
                },
                enabled = !state.isBusy && (isKeysFile || isRemoteKeysFile || !noKeys),
                colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
            ) {
                Text(tx("IMPORT"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(tx("CANCEL"), color = onSurface.copy(alpha = 0.7f))
            }
        }
    )
}

@Composable
internal fun ErrorBar(state: UiState, onClear: () -> Unit) {
    val msg = state.error ?: return
    val localizedMsg = localizeRuntimeMessage(msg)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(0.95f)
            .background(Ember, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localizedMsg, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onClear) {
                Text(tx("OK"), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun localizeRuntimeMessage(msg: String): String {
    val lockoutMatch = Regex("^Too many attempts\\. Try again in (\\d+)s$").matchEntire(msg)
    if (lockoutMatch != null) {
        val seconds = lockoutMatch.groupValues[1]
        return tx("Too many attempts. Try again in {seconds}s").replace("{seconds}", seconds)
    }
    val importedKeysMatch = Regex("^Imported (\\d+) keys$").matchEntire(msg)
    if (importedKeysMatch != null) {
        return tx("Imported {count} keys").replace("{count}", importedKeysMatch.groupValues[1])
    }
    val restoreCompleteMatch = Regex("^Restore complete \\((\\d+) notes\\)$").matchEntire(msg)
    if (restoreCompleteMatch != null) {
        return tx("Restore complete ({count} notes)").replace("{count}", restoreCompleteMatch.groupValues[1])
    }
    val backupRestoredMatch = Regex("^Backup restored \\((\\d+) notes\\)$").matchEntire(msg)
    if (backupRestoredMatch != null) {
        return tx("Backup restored ({count} notes)").replace("{count}", backupRestoredMatch.groupValues[1])
    }
    val remoteImportCompleteMatch = Regex("^Remote import complete \\((\\d+) notes\\)$").matchEntire(msg)
    if (remoteImportCompleteMatch != null) {
        return tx("Remote import complete ({count} notes)").replace("{count}", remoteImportCompleteMatch.groupValues[1])
    }
    val keyManagerRestoredMatch = Regex("^Key manager restored \\((\\d+) keys\\)$").matchEntire(msg)
    if (keyManagerRestoredMatch != null) {
        return tx("Key manager restored ({count} keys)").replace("{count}", keyManagerRestoredMatch.groupValues[1])
    }
    val backupUploadedMatch = Regex("^Backup uploaded \\((\\d+) bytes\\)$").matchEntire(msg)
    if (backupUploadedMatch != null) {
        return tx("Backup uploaded ({count} bytes)").replace("{count}", backupUploadedMatch.groupValues[1])
    }
    val keyImportedViaMatch = Regex("^Key imported via (.+)$").matchEntire(msg)
    if (keyImportedViaMatch != null) {
        return tx("Key imported via {source}").replace("{source}", keyImportedViaMatch.groupValues[1])
    }
    return tx(msg)
}

@Composable
internal fun resolveInfoDialogText(key: String): String {
    return when (key) {
        "info_keys_manager_overview" -> tx(
            "Keys Manager stores keys used for encrypted note sharing and backups.\n\n- OpenPGP key: generated/imported PGP key material.\n- XChaCha key: 32-byte symmetric key used for fast encrypted payload exchange.\n\nSources (manual/qr/nfc) are auto-tagged based on how key was imported."
        )
        "info_manual_import" -> tx(
            "Manual import accepts:\n\n1) OpenPGP armored key blocks (BEGIN PGP ... END PGP)\n2) XChaCha key as:\n- base64 string decoding to exactly 32 bytes, or\n- 64-char hex string (32 bytes)."
        )
        "info_generate_help" -> tx(
            "Generate XChaCha creates a new random 32-byte symmetric key.\n\nGenerate PGP creates a new OpenPGP key pair stored in the app key vault."
        )
        "info_qr_nfc_exchange" -> tx(
            "SHARE SELECTED KEY lets you transfer a selected key to another Nulvex user.\n\n- QR: show code on screen for scan.\n- NFC: writes key payload to NFC tag.\n\nReceiver imports it via QR scanner or NFC read."
        )
        "info_backup_modes" -> tx(
            "Backup modes:\n\n- Local backup: exports encrypted file to your phone storage.\n- Remote encrypted storage (Pro): uploads encrypted backup to Pro remote storage.\n\nIn both cases, decrypt requires the correct key."
        )
        else -> tx(key)
    }
}

@Composable
internal fun LabelsMenu(
    state: UiState,
    onSelectLabel: (String?) -> Unit,
    onAddLabel: (String, String) -> Unit,
    onRemoveLabel: (String, String) -> Unit,
    onCreateStandaloneLabel: (String) -> Unit,
    onClose: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val labels = (state.notes.flatMap { it.labels } + state.savedLabels).distinct().sorted()
    val selectedNote = state.selectedNote
    var labelInput by remember { mutableStateOf("") }
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        modifier = Modifier
            .width(220.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tx("Labels"), style = MaterialTheme.typography.titleMedium, color = onSurface)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onClose) {
                    Text(tx("HIDE"), color = Brass)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Chip(tx("All"), state.activeLabel == null) { onSelectLabel(null) }
            Spacer(modifier = Modifier.height(8.dp))
            labels.forEach { label ->
                val isActive = state.activeLabel == label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { onSelectLabel(label) }) {
                        Text(
                            text = label,
                            color = if (isActive) Brass else onSurface
                        )
                    }
                    if (selectedNote != null) {
                        val hasLabel = selectedNote.labels.contains(label)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = {
                            if (hasLabel) {
                                onRemoveLabel(selectedNote.id, label)
                            } else {
                                onAddLabel(selectedNote.id, label)
                            }
                        }) {
                            Icon(
                                imageVector = if (hasLabel) Icons.Filled.Delete else Icons.Filled.Add,
                                contentDescription = if (hasLabel) stringResource(R.string.notes_cd_remove_label) else stringResource(R.string.notes_cd_add_label),
                                tint = if (hasLabel) Ember else Moss
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (selectedNote != null) tx("Assign label") else tx("Create label"),
                style = MaterialTheme.typography.labelLarge,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    label = { Text(tx("New label")) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (selectedNote != null) {
                            onAddLabel(selectedNote.id, labelInput)
                        } else {
                            onCreateStandaloneLabel(labelInput)
                        }
                        labelInput = ""
                    },
                    enabled = labelInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Text(tx("ADD"))
                }
            }
        }
    }
}


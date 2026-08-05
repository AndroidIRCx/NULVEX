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
internal fun NewNoteScreen(
    state: UiState,
    onCreate: (String, String, List<ChecklistItem>, List<String>, Boolean, List<Uri>, Long?, Boolean, Long?, String?) -> Unit,
    onCancel: () -> Unit,
    defaultExpiry: String,
    defaultReadOnce: Boolean,
    onDraftChanged: (NewNoteDraft?) -> Unit = {}
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val scrollState = rememberScrollState()
    val checklistInputRequester = remember { BringIntoViewRequester() }
    val checklistInputFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var content by remember { mutableStateOf("") }
    var readOnce by remember(defaultReadOnce) { mutableStateOf(defaultReadOnce) }
    var expiryChoice by remember(defaultExpiry) { mutableStateOf(defaultExpiry) }
    var customExpiresAt by remember { mutableStateOf<Long?>(null) }
    var reminderAt by remember { mutableStateOf<Long?>(null) }
    var pinned by remember { mutableStateOf(false) }
    var showChecklist by remember { mutableStateOf(false) }
    var showAttachments by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(false) }
    var showAddMenu by remember { mutableStateOf(false) }
    var showTemplateMenu by remember { mutableStateOf(false) }
    var showShareKeyMenu by remember { mutableStateOf(false) }
    var checklistItems by remember { mutableStateOf(listOf<ChecklistItem>()) }
    var newChecklistItem by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(listOf<Uri>()) }
    var labels by remember { mutableStateOf(listOf<String>()) }
    var newLabel by remember { mutableStateOf("") }
    var selectedShareKeyId by remember {
        mutableStateOf(state.newNoteDraft?.shareKeyId ?: state.sharedKeys.firstOrNull()?.id.orEmpty())
    }
    var pendingChecklistItemScroll by remember { mutableStateOf(false) }
    var pendingChecklistInputReveal by remember { mutableStateOf(false) }
    // Rich-editor state: selection (for cursor-aware formatting inserts), resizable height,
    // and the Edit/Preview toggle. `content` stays the String source of truth.
    var editorSelection by remember { mutableStateOf(TextRange.Zero) }
    var editorHeight by remember { mutableStateOf(300.dp) }
    var showPreview by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    val editorDensity = LocalDensity.current
    val context = LocalContext.current
    var appliedQuickCreate by remember { mutableStateOf<QuickCreateType?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            attachments = attachments + uri
        }
    }
    val voiceInputLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                    ?.trim()
                    .orEmpty()
                if (spokenText.isNotBlank()) {
                    content = if (content.isBlank()) spokenText else "$content\n$spokenText"
                }
            }
        }
    var autoAttachmentLaunchDone by remember { mutableStateOf(false) }
    data class NoteTemplate(
        val name: String,
        val content: String,
        val checklist: List<String> = emptyList(),
        val labels: List<String> = emptyList()
    )
    val templates = listOf(
        NoteTemplate(
            name = tx("Meeting"),
            content = tx("## Meeting\n\n- Agenda:\n- Decisions:\n- Action items:\n- Follow-up:")
        ),
        NoteTemplate(
            name = tx("Checklist"),
            content = "",
            checklist = listOf(tx("First task"), tx("Second task"), tx("Third task"))
        ),
        NoteTemplate(
            name = tx("Journal"),
            content = tx("## Journal\n\nMood:\nWhat happened today:\nWhat I learned:\nNext step:")
        ),
        NoteTemplate(
            name = tx("Credentials"),
            content = tx("Service:\nUsername:\nPassword:\nBackup code:\nNotes:"),
            labels = listOf(tx("security"))
        )
    )

    fun applyTemplate(template: NoteTemplate) {
        content = template.content
        if (template.checklist.isNotEmpty()) {
            showChecklist = true
            checklistItems = template.checklist.map {
                ChecklistItem(id = java.util.UUID.randomUUID().toString(), text = it, checked = false)
            }
            pendingChecklistInputReveal = true
        }
        if (template.labels.isNotEmpty()) {
            showLabels = true
            labels = (labels + template.labels).distinct()
        }
    }

    LaunchedEffect(state.sharedKeys, state.newNoteDraft?.shareKeyId) {
        val draftKeyId = state.newNoteDraft?.shareKeyId
        val preferred = when {
            !draftKeyId.isNullOrBlank() && state.sharedKeys.any { it.id == draftKeyId } -> draftKeyId
            selectedShareKeyId.isNotBlank() && state.sharedKeys.any { it.id == selectedShareKeyId } -> selectedShareKeyId
            else -> state.sharedKeys.firstOrNull()?.id.orEmpty()
        }
        if (preferred != selectedShareKeyId) {
            selectedShareKeyId = preferred
        }
    }

    LaunchedEffect(state.newNoteQuickCreate) {
        if (appliedQuickCreate != state.newNoteQuickCreate) {
            when (state.newNoteQuickCreate) {
                QuickCreateType.CHECKLIST -> {
                    showChecklist = true
                    pendingChecklistInputReveal = true
                }
                QuickCreateType.ATTACHMENT -> {
                    showAttachments = true
                }
                QuickCreateType.TEXT -> Unit
            }
            appliedQuickCreate = state.newNoteQuickCreate
        }
        if (state.newNoteQuickCreate == QuickCreateType.ATTACHMENT && !autoAttachmentLaunchDone) {
            autoAttachmentLaunchDone = true
            imagePicker.launch("image/*")
        }
        if (state.newNoteQuickCreate != QuickCreateType.ATTACHMENT) {
            autoAttachmentLaunchDone = false
        }
    }
    val dateTimeFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

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
                        reminderAt = cal.timeInMillis
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

    val selectedShareKeyLabel = state.sharedKeys.firstOrNull { it.id == selectedShareKeyId }?.label
        ?: state.sharedKeys.firstOrNull()?.label
        ?: tx("No keys available")

    LaunchedEffect(content, checklistItems, labels, pinned, expiryChoice, customExpiresAt, readOnce, reminderAt, selectedShareKeyId) {
        val expiresAtMs = when (expiryChoice) {
            "1h" -> System.currentTimeMillis() + 3_600_000L
            "24h" -> System.currentTimeMillis() + 86_400_000L
            "7d" -> System.currentTimeMillis() + 604_800_000L
            "custom" -> customExpiresAt
            else -> null
        }
        onDraftChanged(
            NewNoteDraft(
                text = content,
                checklist = checklistItems,
                labels = labels,
                pinned = pinned,
                expiresAt = expiresAtMs,
                readOnce = readOnce,
                reminderAt = reminderAt,
                shareKeyId = selectedShareKeyId.ifBlank { null }
            )
        )
    }
    LaunchedEffect(showChecklist, pendingChecklistInputReveal, checklistItems.size) {
        if (showChecklist && pendingChecklistInputReveal) {
            delay(120)
            checklistInputFocusRequester.requestFocus()
            keyboardController?.show()
            checklistInputRequester.bringIntoView()
            delay(120)
            checklistInputRequester.bringIntoView()
            pendingChecklistInputReveal = false
        }
    }
    LaunchedEffect(checklistItems.size, pendingChecklistItemScroll) {
        if (pendingChecklistItemScroll) {
            delay(80)
            scrollState.animateScrollTo(scrollState.maxValue)
            pendingChecklistItemScroll = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(scrollState)
            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Editable note title (Google Keep style): the header itself is the title
                // field. Empty = untitled; the clear (X) resets it.
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge.copy(color = onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                tx("New note"),
                                style = MaterialTheme.typography.titleLarge,
                                color = onSurface.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
                if (title.isNotEmpty()) {
                    IconButton(onClick = { title = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = tx("Clear title"), tint = onSurface.copy(alpha = 0.7f))
                    }
                }
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.Close, contentDescription = tx("Cancel"), tint = onSurface.copy(alpha = 0.7f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { showAddMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text(tx("ADD"))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Button(
                        onClick = { showTemplateMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Coal, contentColor = Sand)
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text(tx("TEMPLATES"))
                    }
                    DropdownMenu(
                        expanded = showTemplateMenu,
                        onDismissRequest = { showTemplateMenu = false }
                    ) {
                        templates.forEach { template ->
                            DropdownMenuItem(
                                text = { Text(template.name) },
                                onClick = {
                                    applyTemplate(template)
                                    showTemplateMenu = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                MarkupChip(if (showPreview) tx("Edit") else tx("Preview")) { showPreview = !showPreview }
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(tx("Checklist")) },
                        onClick = {
                            showChecklist = true
                            pendingChecklistInputReveal = true
                            showAddMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(tx("Image")) },
                        onClick = {
                            showAttachments = true
                            showAddMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(tx("Labels")) },
                        onClick = {
                            showLabels = true
                            showAddMenu = false
                        }
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (showChecklist) {
                        Text(tx("Checklist"), color = Moss.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    }
                    if (showAttachments) {
                        Text(tx("Images"), color = Moss.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    }
                    if (showLabels || labels.isNotEmpty()) {
                        Text(tx("Labels"), color = Moss.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            fun applyWrap(before: String, after: String, placeholder: String) {
                val len = content.length
                val start = editorSelection.min.coerceIn(0, len)
                val end = editorSelection.max.coerceIn(0, len)
                val selected = content.substring(start, end).ifEmpty { placeholder }
                content = content.substring(0, start) + before + selected + after + content.substring(end)
                editorSelection = TextRange(start + before.length + selected.length + after.length)
            }
            fun applyLinePrefix(prefix: String) {
                val len = content.length
                val pos = editorSelection.min.coerceIn(0, len)
                val lineStart = content.lastIndexOf('\n', (pos - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
                content = content.substring(0, lineStart) + prefix + content.substring(lineStart)
                editorSelection = TextRange(pos + prefix.length)
            }
            // Precompute placeholder strings here (tx() is @Composable and can't run inside onClick).
            val phBold = tx("bold")
            val phItalic = tx("italic")
            val phCode = tx("code")
            val phText = tx("text")
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            ) {
                MarkupChip("B") { applyWrap("**", "**", phBold) }
                MarkupChip("I") { applyWrap("*", "*", phItalic) }
                MarkupChip("H") { applyLinePrefix("# ") }
                MarkupChip("•") { applyLinePrefix("- ") }
                MarkupChip("❝") { applyLinePrefix("> ") }
                MarkupChip("</>") { applyWrap("`", "`", phCode) }
                MarkupChip("{ }") { applyWrap("\n```\n", "\n```\n", phCode) }
                MarkupChip("link") { applyWrap("[", "](https://)", phText) }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { showPreview = !showPreview }) {
                    Text(if (showPreview) tx("EDIT") else tx("PREVIEW"), color = Moss)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (showPreview) {
                val previewModifier = Modifier
                    .fillMaxWidth()
                    .height(editorHeight)
                    .border(1.dp, onSurface.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                if (NoteMarkup.containsHtml(content)) {
                    val bg = MaterialTheme.colorScheme.surface
                    val accent = MaterialTheme.colorScheme.primary
                    val codeBg = MaterialTheme.colorScheme.surfaceVariant
                    val html = remember(content, bg, onSurface, accent, codeBg) {
                        NoteMarkup.toHtmlDocument(
                            content,
                            backgroundHex = bg.toCssHex(),
                            textHex = onSurface.toCssHex(),
                            accentHex = accent.toCssHex(),
                            codeBgHex = codeBg.toCssHex()
                        )
                    }
                    SandboxedHtmlPreview(html = html, modifier = previewModifier)
                } else {
                    val primary = MaterialTheme.colorScheme.primary
                    if (content.isBlank()) {
                        Box(modifier = previewModifier) {
                            Text(tx("Nothing to preview"), color = onSurface.copy(alpha = 0.5f))
                        }
                    } else {
                        val rendered = remember(content, primary) {
                            NoteMarkup.toAnnotatedString(content, linkColor = primary, codeColor = primary)
                        }
                        Box(modifier = previewModifier.verticalScroll(rememberScrollState())) {
                            Text(rendered, color = onSurface)
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = TextFieldValue(
                        text = content,
                        selection = TextRange(
                            editorSelection.start.coerceIn(0, content.length),
                            editorSelection.end.coerceIn(0, content.length)
                        )
                    ),
                    onValueChange = {
                        content = it.text
                        editorSelection = it.selection
                    },
                    label = { Text(tx("Write safely...")) },
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(editorHeight)
                        .bringIntoViewOnFocus()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaDp = with(editorDensity) { dragAmount.y.toDp() }
                                editorHeight = (editorHeight + deltaDp).coerceIn(160.dp, 640.dp)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(onSurface.copy(alpha = 0.3f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            val voicePrompt = stringResource(R.string.notes_voice_prompt)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = {
                        val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(RecognizerIntent.EXTRA_PROMPT, voicePrompt)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                        }
                        runCatching { voiceInputLauncher.launch(voiceIntent) }
                    }
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = stringResource(R.string.notes_cd_voice_input), tint = Moss)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pinned, onCheckedChange = { pinned = it })
                Text(tx("Pin to top"), color = onSurface)
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (showChecklist) {
                Text(tx("Checklist"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                if (checklistItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    checklistItems.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = item.checked,
                                onCheckedChange = { checked ->
                                    checklistItems = checklistItems.map {
                                        if (it.id == item.id) it.copy(checked = checked) else it
                                    }
                                }
                            )
                            Text(item.text, color = onSurface, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                checklistItems = checklistItems.filterNot { it.id == item.id }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = Ember,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(tx("REMOVE"), color = Ember)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (showAttachments) {
                Text(tx("Attachments"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { imagePicker.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text(tx("ADD IMAGE"))
                    }
                    if (attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            tx("{count} attached").replace("{count}", attachments.size.toString()),
                            color = onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                if (attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    attachments.forEach { uri ->
                        val name = resolveDisplayName(context, uri) ?: tx("Image")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(name, color = onSurface, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                attachments = attachments.filterNot { it == uri }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = Ember,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(tx("REMOVE"), color = Ember)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (showLabels || labels.isNotEmpty()) {
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
                            if (trimmed.isNotBlank() && !labels.contains(trimmed)) {
                                labels = labels + trimmed
                                newLabel = ""
                            }
                        },
                        enabled = newLabel.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text(tx("ADD"))
                    }
                }
                val labelSuggestions = if (newLabel.isNotBlank()) {
                    state.savedLabels.filter {
                        it.contains(newLabel.trim(), ignoreCase = true) && !labels.contains(it)
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
                                        labels = labels + suggestion
                                        newLabel = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(suggestion, style = MaterialTheme.typography.labelMedium, color = Moss)
                            }
                        }
                    }
                }
                if (labels.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        labels.forEach { label ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Moss.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)
                            ) {
                                Text(label, color = Moss, style = MaterialTheme.typography.labelLarge)
                                IconButton(
                                    onClick = { labels = labels.filterNot { it == label } },
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
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = readOnce, onCheckedChange = { readOnce = it })
                Text(tx("Read once"), color = onSurface)
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
            if (customExpiresAt != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    tx("Selected expiry: {value}")
                        .replace("{value}", dateTimeFormatter.format(java.util.Date(customExpiresAt!!))),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.notes_section_metadata),
                style = MaterialTheme.typography.labelLarge,
                color = onSurface.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { openReminderPicker() },
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Icon(imageVector = Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text(tx("SET REMINDER"))
                }
                if (reminderAt != null) {
                    TextButton(onClick = { reminderAt = null }) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsOff,
                            contentDescription = null,
                            tint = Ember,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(tx("CLEAR REMINDER"), color = Ember)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(tx("Share key"), style = MaterialTheme.typography.labelLarge, color = onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            if (state.sharedKeys.isEmpty()) {
                Text(
                    tx("No keys available. Add a key in Keys Manager before sharing this note."),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ember
                )
            } else {
                Box {
                    Button(
                        onClick = { showShareKeyMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Coal, contentColor = Sand),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.Lock, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text(selectedShareKeyLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    DropdownMenu(
                        expanded = showShareKeyMenu,
                        onDismissRequest = { showShareKeyMenu = false }
                    ) {
                        state.sharedKeys.forEach { key ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(key.label, modifier = Modifier.weight(1f))
                                        if (selectedShareKeyId == key.id) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Brass
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedShareKeyId = key.id
                                    showShareKeyMenu = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    tx("This key will be used when sharing this note."),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }
            if (reminderAt != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    tx("Reminder: {value}")
                        .replace("{value}", dateTimeFormatter.format(java.util.Date(reminderAt!!))),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            }
            if (showChecklist) {
                HorizontalDivider(color = onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newChecklistItem,
                        onValueChange = { newChecklistItem = it },
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
                            val trimmed = newChecklistItem.trim()
                            if (trimmed.isNotBlank()) {
                                checklistItems = checklistItems + ChecklistItem(
                                    id = java.util.UUID.randomUUID().toString(),
                                    text = trimmed,
                                    checked = false
                                )
                                newChecklistItem = ""
                                pendingChecklistItemScroll = true
                                pendingChecklistInputReveal = true
                            }
                        },
                        enabled = newChecklistItem.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                        Text(tx("ADD"))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            val expiresAt = when (expiryChoice) {
                "1h" -> System.currentTimeMillis() + 3_600_000L
                "24h" -> System.currentTimeMillis() + 86_400_000L
                "7d" -> System.currentTimeMillis() + 604_800_000L
                "custom" -> customExpiresAt
                else -> null
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onCancel,
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Coal, contentColor = Sand),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text(tx("CANCEL"))
                }
                Button(
                    onClick = {
                        onCreate(
                            title.trim(),
                            content,
                            checklistItems,
                            labels,
                            pinned,
                            attachments,
                            expiresAt,
                            readOnce,
                            reminderAt,
                            selectedShareKeyId.ifBlank { null }
                        )
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    },
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text(tx("SAVE NOTE"))
                }
            }
        }
    }
}


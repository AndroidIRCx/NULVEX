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


internal fun formatEventTime(ts: Long): String {
    if (ts == 0L) return ""
    val diffSec = (System.currentTimeMillis() - ts) / 1000L
    return when {
        diffSec < 60 -> "just now"
        diffSec < 3600 -> "${diffSec / 60}m ago"
        diffSec < 86400 -> "${diffSec / 3600}h ago"
        else -> "${diffSec / 86400}d ago"
    }
}



@Composable
internal fun formatExpiryBadge(expiresAt: Long): String {
    val remaining = expiresAt - System.currentTimeMillis()
    if (remaining <= 0L) return tx("Next expiry: overdue")
    val minutes = max(1, remaining / 60_000L)
    val hours = remaining / 3_600_000L
    val days = remaining / 86_400_000L
    return when {
        days >= 1 -> tx("Next expiry: {value}d").replace("{value}", days.toString())
        hours >= 1 -> tx("Next expiry: {value}h").replace("{value}", hours.toString())
        else -> tx("Next expiry: {value}m").replace("{value}", minutes.toString())
    }
}

@Composable
internal fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCreate: () -> Unit,
    onQuickCreate: (QuickCreateType) -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var quickCreateExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = tx("Search"),
            tint = onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(tx("Search notes"), color = onSurface.copy(alpha = 0.6f)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        IconButton(onClick = { quickCreateExpanded = true }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = tx("New note"),
                tint = onSurface
            )
        }
        DropdownMenu(
            expanded = quickCreateExpanded,
            onDismissRequest = { quickCreateExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(tx("Text note")) },
                onClick = {
                    quickCreateExpanded = false
                    onCreate()
                }
            )
            DropdownMenuItem(
                text = { Text(tx("Checklist note")) },
                onClick = {
                    quickCreateExpanded = false
                    onQuickCreate(QuickCreateType.CHECKLIST)
                }
            )
            DropdownMenuItem(
                text = { Text(tx("Attachment note")) },
                onClick = {
                    quickCreateExpanded = false
                    onQuickCreate(QuickCreateType.ATTACHMENT)
                }
            )
        }
    }
}

@Composable
internal fun LabelFilters(
    labels: List<String>,
    activeLabel: String?,
    onSelectLabel: (String?) -> Unit
) {
    val scroll = rememberScrollState()
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(scroll)
    ) {
        Chip(tx("All"), activeLabel == null) { onSelectLabel(null) }
        labels.forEach { label ->
            Chip(label, activeLabel == label) { onSelectLabel(label) }
        }
    }
}


@Composable
internal fun NoteCard(
    note: Note,
    selected: Boolean,
    selectionMode: Boolean,
    onToggleSelection: () -> Unit,
    onTogglePinned: (String) -> Unit,
    onOpen: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val preview = note.text.trim().replace("\n", " ")
    val checklistPreview = note.checklist.take(3)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Brass.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (selectionMode) onToggleSelection() else onOpen()
                },
                onLongClick = onToggleSelection
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                val previewText = when {
                    preview.isNotBlank() -> preview
                    checklistPreview.isNotEmpty() -> tx("Checklist note")
                    note.attachments.isNotEmpty() -> tx("Image note")
                    else -> tx("Empty note")
                }
                Text(
                    text = previewText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onTogglePinned(note.id) }) {
                    Icon(
                        imageVector = if (note.pinned) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (note.pinned) stringResource(R.string.notes_cd_unpin) else stringResource(R.string.notes_cd_pin),
                        tint = if (note.pinned) Brass else onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            if (checklistPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                checklistPreview.forEach { item ->
                    val marker = if (item.checked) "[x]" else "[ ]"
                    Text(
                        text = "$marker ${item.text}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = onSurface.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (note.labels.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    note.labels.take(3).forEach { label ->
                        LabelPill(label)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (note.pinned) {
                    Text(
                        text = tx("PINNED"),
                        style = MaterialTheme.typography.labelLarge,
                        color = Brass
                    )
                }
                if (note.archivedAt != null) {
                    Text(
                        text = tx("ARCHIVED"),
                        style = MaterialTheme.typography.labelLarge,
                        color = onSurface.copy(alpha = 0.7f)
                    )
                }
                if (note.readOnce) {
                    Text(
                        text = tx("Read once"),
                        style = MaterialTheme.typography.labelLarge,
                        color = Brass
                    )
                }
                if (note.expiresAt != null) {
                    Text(
                        text = tx("EXPIRING"),
                        style = MaterialTheme.typography.labelLarge,
                        color = Ember
                    )
                }
                if (note.reminderAt != null && !note.reminderDone) {
                    Text(
                        text = tx("REMINDER"),
                        style = MaterialTheme.typography.labelLarge,
                        color = Moss
                    )
                }
                if (note.attachments.isNotEmpty()) {
                    Text(
                        text = tx("{count} IMG").replace("{count}", note.attachments.size.toString()),
                        style = MaterialTheme.typography.labelLarge,
                        color = onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


@Composable
internal fun EmptySearchState() {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = tx("No matches"),
                color = onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tx("Try a different search or label filter."),
                color = onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

internal fun resolveDisplayName(context: android.content.Context, uri: Uri): String? {
    val resolver = context.contentResolver
    val cursor = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) return it.getString(index)
        }
    }
    return null
}

internal fun generateQrBitmap(content: String, size: Int): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.L,
            EncodeHintType.MARGIN to 1
        )
        val matrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp
    } catch (_: Exception) {
        null
    }
}
@Composable
internal fun EmptyVaultState(onOpenNew: () -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = Brass.copy(alpha = 0.6f),
                modifier = Modifier.height(64.dp).width(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = tx("Your vault is ready"),
                color = onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tx("Create your first encrypted note"),
                color = onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Feature hints
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureHint(
                    icon = Icons.Filled.Schedule,
                    text = tx("Set notes to self-destruct after 1h, 24h, or 7 days")
                )
                FeatureHint(
                    icon = Icons.Filled.Security,
                    text = tx("Read-once notes are deleted after viewing")
                )
                FeatureHint(
                    icon = Icons.Filled.Star,
                    text = tx("Swipe right to pin, swipe left to delete")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onOpenNew,
                colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(tx("CREATE FIRST NOTE"))
            }
        }
    }
}



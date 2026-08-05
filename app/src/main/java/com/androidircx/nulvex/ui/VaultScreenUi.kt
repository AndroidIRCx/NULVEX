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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun VaultScreen(
    state: UiState,
    onOpenNew: () -> Unit,
    onQuickCreate: (QuickCreateType) -> Unit,
    onOpenNote: (String) -> Unit,
    onToggleNoteSelection: (String) -> Unit,
    onClearNoteSelection: () -> Unit,
    onBulkArchiveSelected: () -> Unit,
    onBulkDeleteSelected: () -> Unit,
    onBulkAddLabelSelected: (String) -> Unit,
    onBulkSetReminderSelected: (Long) -> Unit,
    onTogglePinned: (String) -> Unit,
    onDelete: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSetShowArchived: (Boolean) -> Unit,
    onSetShowTrash: (Boolean) -> Unit
) {
    var pendingReadOnce by remember { mutableStateOf<Note?>(null) }
    var pendingDelete by remember { mutableStateOf<Note?>(null) }
    var sortMode by remember { mutableStateOf(SortMode.RECENTLY_EDITED) }
    var showSortMenu by remember { mutableStateOf(false) }
    var hasReminderOnly by remember { mutableStateOf(false) }
    var createdRange by remember { mutableStateOf(CreatedRangeFilter.ANY) }
    var groupByLabel by remember { mutableStateOf(false) }
    var showCalendarView by remember { mutableStateOf(false) }
    var showBulkLabelDialog by remember { mutableStateOf(false) }
    var bulkLabelInput by remember { mutableStateOf("") }
    val sortLabels = mapOf(
        SortMode.RECENTLY_EDITED to stringResource(R.string.notes_sort_recently_edited),
        SortMode.EXPIRING_SOON to stringResource(R.string.notes_sort_expiring_soon),
        SortMode.REMINDER_DUE to stringResource(R.string.notes_sort_reminder_due),
        SortMode.PINNED_FIRST to stringResource(R.string.notes_sort_pinned_first)
    )

    BoxWithConstraints {
        val compact = maxWidth < 360.dp
        val badgeSpacing = if (compact) 8.dp else 10.dp
        val sectionGap = if (compact) 10.dp else 12.dp
        val listTopPadding = if (compact) 16.dp else 20.dp
        val onSurface = MaterialTheme.colorScheme.onSurface
        val unlabeledGroupName = tx("Unlabeled")
        val pinnedGroupLabel = tx("Pinned")

        val sortedNotes = state.notes
            .filter { note ->
                note.matchesQuery(state.searchQuery) &&
                    (state.activeLabel == null || note.labels.contains(state.activeLabel)) &&
                    (!hasReminderOnly || (note.reminderAt != null && !note.reminderDone)) &&
                    when (createdRange) {
                        CreatedRangeFilter.ANY -> true
                        CreatedRangeFilter.LAST_24H -> note.createdAt >= System.currentTimeMillis() - 24L * 60L * 60L * 1000L
                        CreatedRangeFilter.LAST_7D -> note.createdAt >= System.currentTimeMillis() - 7L * 24L * 60L * 60L * 1000L
                        CreatedRangeFilter.LAST_30D -> note.createdAt >= System.currentTimeMillis() - 30L * 24L * 60L * 60L * 1000L
                    }
            }
            .let { notes ->
                when (sortMode) {
                    SortMode.RECENTLY_EDITED -> notes.sortedByDescending { it.updatedAt }
                    SortMode.EXPIRING_SOON -> notes.sortedBy { it.expiresAt ?: Long.MAX_VALUE }
                    SortMode.REMINDER_DUE -> notes.sortedBy { it.reminderAt ?: Long.MAX_VALUE }
                    SortMode.PINNED_FIRST -> notes.sortedWith(
                        compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt }
                    )
                }
            }
        val (pinnedNotes, otherNotes) = sortedNotes.partition { it.pinned }

        // Statistics
        val readOnceCount = sortedNotes.count { it.readOnce }
        val expiringCount = sortedNotes.count { it.expiresAt != null }
        val nextExpiry = sortedNotes.mapNotNull { it.expiresAt }.minOrNull()

        Column {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChange,
                onCreate = onOpenNew,
                onQuickCreate = onQuickCreate
            )

            Spacer(modifier = Modifier.height(sectionGap))

            if (state.selectedNoteIds.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = onSurface.copy(alpha = 0.07f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            tx("{count} selected").replace("{count}", state.selectedNoteIds.size.toString()),
                            color = onSurface,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onBulkArchiveSelected) {
                            Icon(Icons.Filled.Archive, contentDescription = stringResource(R.string.notes_cd_archive_selected), tint = Brass)
                        }
                        IconButton(onClick = onBulkDeleteSelected) {
                            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.notes_cd_delete_selected), tint = Ember)
                        }
                        IconButton(onClick = { showBulkLabelDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.Label, contentDescription = stringResource(R.string.notes_cd_label_selected), tint = Moss)
                        }
                        IconButton(onClick = { onBulkSetReminderSelected(System.currentTimeMillis() + 3_600_000L) }) {
                            Icon(Icons.Filled.Schedule, contentDescription = stringResource(R.string.notes_cd_reminder_plus_1h), tint = Moss)
                        }
                        IconButton(onClick = onClearNoteSelection) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.notes_cd_clear_selection), tint = onSurface.copy(alpha = 0.7f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(sectionGap))
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Chip(stringResource(R.string.notes_tab_active), !state.showArchived && !state.showTrash) {
                    onSetShowTrash(false)
                    onSetShowArchived(false)
                }
                Chip(stringResource(R.string.notes_tab_archived), state.showArchived && !state.showTrash) {
                    onSetShowTrash(false)
                    onSetShowArchived(true)
                }
                Chip(stringResource(R.string.notes_tab_trash), state.showTrash) {
                    onSetShowTrash(true)
                }
            }

            Spacer(modifier = Modifier.height(sectionGap))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Chip(stringResource(R.string.notes_filter_has_reminder), hasReminderOnly) { hasReminderOnly = !hasReminderOnly }
                Chip(stringResource(R.string.notes_filter_group_by_label), groupByLabel) { groupByLabel = !groupByLabel }
                Chip(stringResource(R.string.notes_filter_calendar), showCalendarView) { showCalendarView = !showCalendarView }
                Chip(
                    when (createdRange) {
                        CreatedRangeFilter.ANY -> tx("Any date")
                        CreatedRangeFilter.LAST_24H -> tx("Last 24h")
                        CreatedRangeFilter.LAST_7D -> tx("Last 7d")
                        CreatedRangeFilter.LAST_30D -> tx("Last 30d")
                    },
                    createdRange != CreatedRangeFilter.ANY
                ) {
                    createdRange = when (createdRange) {
                        CreatedRangeFilter.ANY -> CreatedRangeFilter.LAST_24H
                        CreatedRangeFilter.LAST_24H -> CreatedRangeFilter.LAST_7D
                        CreatedRangeFilter.LAST_7D -> CreatedRangeFilter.LAST_30D
                        CreatedRangeFilter.LAST_30D -> CreatedRangeFilter.ANY
                    }
                }
            }

            Spacer(modifier = Modifier.height(sectionGap))

            val now = System.currentTimeMillis()
            val createdToday = sortedNotes.count { it.createdAt >= now - 24L * 60L * 60L * 1000L }
            val createdWeek = sortedNotes.count { it.createdAt >= now - 7L * 24L * 60L * 60L * 1000L }
            val dueToday = sortedNotes.count { it.reminderAt != null && it.reminderAt <= now + 24L * 60L * 60L * 1000L }
            val currentStreak = computeCurrentCreationStreak(sortedNotes, now)
            val bestStreak = computeBestCreationStreak(sortedNotes)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                VaultBadge(text = stringResource(R.string.notes_badge_today_prefix) + " $createdToday", tint = Moss)
                VaultBadge(text = stringResource(R.string.notes_badge_7d_prefix) + " $createdWeek", tint = Brass)
                VaultBadge(text = stringResource(R.string.notes_badge_due_soon_prefix) + " $dueToday", tint = Ember)
                if (currentStreak > 0) {
                    VaultBadge(text = stringResource(R.string.notes_badge_streak_prefix) + " $currentStreak", tint = Moss)
                }
                if (bestStreak > 1) {
                    VaultBadge(text = tx("Best: {count}").replace("{count}", bestStreak.toString()), tint = Sand)
                }
            }

            Spacer(modifier = Modifier.height(sectionGap))

            if (showCalendarView) {
                val dateKeyFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
                val events = buildList {
                    sortedNotes.forEach { n ->
                        n.reminderAt?.let { add(Triple(it, tx("Reminder"), n)) }
                        n.expiresAt?.let { add(Triple(it, tx("Expiry"), n)) }
                    }
                }.sortedBy { it.first }
                if (events.isEmpty()) {
                    Text(tx("No reminder/expiry events"), color = onSurface.copy(alpha = 0.7f))
                } else {
                    val groupedEvents = events.groupBy { dateKeyFmt.format(java.util.Date(it.first)) }
                    groupedEvents.forEach { (day, dayEvents) ->
                        SectionLabel(day)
                        dayEvents.forEach { event ->
                            val title = event.third.text.lineSequence().firstOrNull { it.trim().isNotBlank() }?.trim().orEmpty()
                            Text(
                                text = tx("{time} {label}: {title}")
                                    .replace("{time}", timeFmt.format(java.util.Date(event.first)))
                                    .replace("{label}", event.second)
                                    .replace("{title}", if (title.isBlank()) event.third.id else title),
                                color = onSurface.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(sectionGap))
            }

            // Stats and sort row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(badgeSpacing)) {
                    val notesBadge = pluralStringResource(
                        id = R.plurals.notes_count,
                        count = sortedNotes.size,
                        sortedNotes.size
                    )
                    VaultBadge(text = notesBadge, tint = Sand)
                    if (readOnceCount > 0) {
                        VaultBadge(text = tx("{count} burn").replace("{count}", readOnceCount.toString()), tint = Brass)
                    }
                    if (expiringCount > 0) {
                        VaultBadge(text = tx("{count} expiring").replace("{count}", expiringCount.toString()), tint = Ember)
                    }
                }

                // Sort button
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showSortMenu = true }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = tx("Sort"),
                            tint = onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.height(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            sortLabels.getValue(sortMode),
                            style = MaterialTheme.typography.labelMedium,
                            color = onSurface.copy(alpha = 0.7f)
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        SortMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        sortLabels.getValue(mode),
                                        color = if (mode == sortMode) Brass else onSurface
                                    )
                                },
                                onClick = {
                                    sortMode = mode
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (nextExpiry != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatExpiryBadge(nextExpiry),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ember.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(sectionGap))

            if (state.notes.isEmpty()) {
                EmptyVaultState(onOpenNew)
                return@BoxWithConstraints
            }
            if (sortedNotes.isEmpty()) {
                EmptySearchState()
                return@BoxWithConstraints
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = listTopPadding, bottom = 16.dp)
            ) {
                if (groupByLabel) {
                    val grouped = sortedNotes
                        .groupBy { it.labels.firstOrNull()?.trim().orEmpty().ifBlank { unlabeledGroupName } }
                        .toSortedMap()
                    grouped.forEach { (groupName, notesInGroup) ->
                        item(key = "group_header_$groupName") { SectionLabel(groupName) }
                        val groupedOrdered = notesInGroup.partition { it.pinned }.let { it.first + it.second }
                        items(groupedOrdered, key = { "${groupName}_${it.id}" }) { note ->
                            SwipeableNoteCard(
                                note = note,
                                selected = state.selectedNoteIds.contains(note.id),
                                selectionMode = state.selectedNoteIds.isNotEmpty(),
                                onToggleSelection = { onToggleNoteSelection(note.id) },
                                onTogglePinned = onTogglePinned,
                                onDelete = { pendingDelete = note },
                                onOpen = {
                                    if (note.readOnce) {
                                        pendingReadOnce = note
                                    } else {
                                        onOpenNote(note.id)
                                    }
                                },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                } else {
                    if (sortMode == SortMode.PINNED_FIRST && pinnedNotes.isNotEmpty()) {
                        item(key = "pinned_header") { SectionLabel(pinnedGroupLabel) }
                    }
                    val notesToRender = if (sortMode == SortMode.PINNED_FIRST) {
                        pinnedNotes + otherNotes
                    } else {
                        sortedNotes
                    }
                    items(notesToRender, key = { it.id }) { note ->
                        if (sortMode == SortMode.PINNED_FIRST && note == otherNotes.firstOrNull()) {
                            SectionLabel(if (pinnedNotes.isEmpty()) tx("Notes") else tx("Others"))
                        }
                        SwipeableNoteCard(
                            note = note,
                            selected = state.selectedNoteIds.contains(note.id),
                            selectionMode = state.selectedNoteIds.isNotEmpty(),
                            onToggleSelection = { onToggleNoteSelection(note.id) },
                            onTogglePinned = onTogglePinned,
                            onDelete = { pendingDelete = note },
                            onOpen = {
                                if (note.readOnce) {
                                    pendingReadOnce = note
                                } else {
                                    onOpenNote(note.id)
                                }
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }

    if (showBulkLabelDialog) {
        AlertDialog(
            onDismissRequest = { showBulkLabelDialog = false },
            title = { Text(tx("Add label to selected")) },
            text = {
                OutlinedTextField(
                    value = bulkLabelInput,
                    onValueChange = { bulkLabelInput = it },
                    singleLine = true,
                    label = { Text(tx("Label")) }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBulkAddLabelSelected(bulkLabelInput)
                        bulkLabelInput = ""
                        showBulkLabelDialog = false
                    },
                    enabled = bulkLabelInput.isNotBlank()
                ) { Text(tx("APPLY"), color = Brass) }
            },
            dismissButton = {
                TextButton(onClick = { showBulkLabelDialog = false }) {
                    Text(tx("CANCEL"))
                }
            }
        )
    }

    // Read-once confirmation dialog
    if (pendingReadOnce != null) {
        Dialog(onDismissRequest = { pendingReadOnce = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Coal)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timer,
                        contentDescription = null,
                        tint = Brass,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(tx("BURN NOTE"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Brass
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(tx("This note is set to read-once. It will be permanently destroyed after you close it."),
                        color = Sand.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val id = pendingReadOnce?.id
                            pendingReadOnce = null
                            if (id != null) onOpenNote(id)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text(tx("OPEN & BURN"), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { pendingReadOnce = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(tx("CANCEL"), color = Sand.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = {
                Text(
                    if (state.showTrash) tx("Delete permanently?") else tx("Move to trash?"),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    if (state.showTrash) tx("This action cannot be undone.") else tx("The note will stay in Trash for 7 days."),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = pendingDelete?.id
                        pendingDelete = null
                        if (id != null) onDelete(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Ember, contentColor = Sand)
                ) {
                    Text(tx("DELETE"))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(tx("CANCEL"), color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
internal fun SwipeableNoteCard(
    note: Note,
    selected: Boolean,
    selectionMode: Boolean,
    onToggleSelection: () -> Unit,
    onTogglePinned: (String) -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                onDelete()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.StartToEnd -> {
                onTogglePinned(note.id)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> Ember.copy(alpha = 0.9f)
                    SwipeToDismissBoxValue.StartToEnd -> if (note.pinned) Sand.copy(alpha = 0.7f) else Brass.copy(alpha = 0.9f)
                    else -> Color.Transparent
                },
                label = "swipeColor"
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.CenterStart
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Icons.Filled.Delete
                SwipeToDismissBoxValue.StartToEnd -> if (note.pinned) Icons.Outlined.StarBorder else Icons.Filled.Star
                else -> Icons.Filled.Star
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Sand
                )
            }
        },
        modifier = modifier
    ) {
        NoteCard(
            note = note,
            selected = selected,
            selectionMode = selectionMode,
            onToggleSelection = onToggleSelection,
            onTogglePinned = onTogglePinned,
            onOpen = onOpen
        )
    }
}


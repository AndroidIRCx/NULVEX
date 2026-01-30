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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.ui.graphics.asImageBitmap
import com.androidircx.nulvex.data.ChecklistItem
import com.androidircx.nulvex.data.Note
import com.androidircx.nulvex.ui.theme.Brass
import com.androidircx.nulvex.ui.theme.Coal
import com.androidircx.nulvex.ui.theme.Ember
import com.androidircx.nulvex.ui.theme.Ink
import com.androidircx.nulvex.ui.theme.Moss
import com.androidircx.nulvex.ui.theme.Sand
import com.androidircx.nulvex.ui.theme.ThemeMode
import kotlin.math.max
import android.net.Uri
import android.provider.OpenableColumns

@Composable
fun MainScreen(
    state: UiState,
    onSetup: (String, String?) -> Unit,
    onUnlock: (String) -> Unit,
    onLock: () -> Unit,
    onPanic: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onUpdateDecoyPin: (String, String) -> Unit,
    onDisableDecoy: () -> Unit,
    onUpdateLockTimeout: (Long) -> Unit,
    onUpdateDefaultExpiry: (String) -> Unit,
    onUpdateDefaultReadOnce: (Boolean) -> Unit,
    onRequestBiometricEnroll: (String) -> Unit,
    onRequestBiometricUnlock: () -> Unit,
    onDisableBiometric: () -> Unit,
    onChangeRealPin: (String, String, String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onOpenNew: () -> Unit,
    onCreate: (String, List<ChecklistItem>, List<String>, Boolean, List<android.net.Uri>, Long?, Boolean) -> Unit,
    onOpenNote: (String) -> Unit,
    onCloseNote: () -> Unit,
    onDelete: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleChecklistItem: (String, String) -> Unit,
    onAddChecklistItem: (String, String) -> Unit,
    onRemoveChecklistItem: (String, String) -> Unit,
    onUpdateChecklistText: (String, String, String) -> Unit,
    onMoveChecklistItem: (String, String, Int) -> Unit,
    onAddLabel: (String, String) -> Unit,
    onRemoveLabel: (String, String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectLabel: (String?) -> Unit,
    onLoadAttachmentPreview: (String, String) -> Unit,
    onRemoveAttachment: (String, String) -> Unit,
    onClearError: () -> Unit
) {
    var showPanicConfirm by remember { mutableStateOf(false) }
    var showLabelMenu by remember { mutableStateOf(false) }
    AppBackground {
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = showLabelMenu) {
                LabelsMenu(
                    state = state,
                    onSelectLabel = onSelectLabel,
                    onAddLabel = onAddLabel,
                    onRemoveLabel = onRemoveLabel,
                    onClose = { showLabelMenu = false }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                TopHeader(
                    state = state,
                    onLock = onLock,
                    onPanicClick = { showPanicConfirm = true },
                    onOpenSettings = onOpenSettings,
                    onCloseSettings = onCloseSettings,
                    onToggleLabels = { showLabelMenu = !showLabelMenu }
                )
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350)) { 40 }
                ) {
                    when (state.screen) {
                        Screen.Setup -> SetupScreen(state, onSetup, onRequestBiometricEnroll)
                        Screen.Unlock -> UnlockScreen(state, onUnlock, onRequestBiometricUnlock)
                        Screen.Vault -> VaultScreen(
                            state,
                            onOpenNew,
                            onOpenNote,
                            onTogglePinned,
                            onSearchQueryChange
                        )
                        Screen.Settings -> SettingsScreen(
                            state = state,
                            onUpdateDecoyPin = onUpdateDecoyPin,
                            onDisableDecoy = onDisableDecoy,
                            onUpdateLockTimeout = onUpdateLockTimeout,
                            onUpdateDefaultExpiry = onUpdateDefaultExpiry,
                            onUpdateDefaultReadOnce = onUpdateDefaultReadOnce,
                            onDisableBiometric = onDisableBiometric,
                            onRequestBiometricEnroll = onRequestBiometricEnroll,
                            onChangeRealPin = onChangeRealPin,
                            onUpdateThemeMode = onUpdateThemeMode,
                            onClose = onCloseSettings
                        )
                        Screen.NewNote -> NewNoteScreen(
                            state = state,
                            onCreate = onCreate,
                            defaultExpiry = state.defaultExpiry,
                            defaultReadOnce = state.defaultReadOnce
                        )
                        Screen.NoteDetail -> NoteDetailScreen(
                            state,
                            onCloseNote,
                            onDelete,
                            onTogglePinned,
                            onToggleChecklistItem,
                            onAddChecklistItem,
                            onRemoveChecklistItem,
                            onUpdateChecklistText,
                            onMoveChecklistItem,
                            onLoadAttachmentPreview,
                            onRemoveAttachment
                        )
                    }
                }
            }
        }
        ErrorBar(state, onClearError)
        if (showPanicConfirm) {
            AlertDialog(
                onDismissRequest = { showPanicConfirm = false },
                title = { Text("Panic wipe", color = Sand) },
                text = {
                    Text(
                        "This will permanently delete all vault data on this device.",
                        color = Sand.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPanicConfirm = false
                            onPanic()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Ember, contentColor = Sand)
                    ) {
                        Text("WIPE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPanicConfirm = false }) {
                        Text("CANCEL", color = Sand)
                    }
                },
                containerColor = Coal
            )
        }
    }
}

@Composable
private fun AppBackground(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val gradient = Brush.linearGradient(
        colors = listOf(colors.background, colors.surface, colors.secondary)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.primary.copy(alpha = 0.12f), Color.Transparent),
                        radius = 900f
                    )
                )
        )
        content()
    }
}

@Composable
private fun TopHeader(
    state: UiState,
    onLock: () -> Unit,
    onPanicClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onCloseSettings: () -> Unit,
    onToggleLabels: () -> Unit
) {
    val onBackground = MaterialTheme.colorScheme.onBackground
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "NULVEX",
                style = MaterialTheme.typography.displayLarge,
                color = onBackground
            )
            Text(
                text = "Offline secure vault",
                style = MaterialTheme.typography.labelLarge,
                color = onBackground.copy(alpha = 0.7f)
            )
        }
        if (state.screen == Screen.Vault || state.screen == Screen.NoteDetail || state.screen == Screen.NewNote) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onToggleLabels) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = "Labels",
                        tint = onBackground.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = onBackground.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onPanicClick) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Panic wipe",
                        tint = Ember
                    )
                }
                IconButton(onClick = onLock) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock",
                        tint = Brass
                    )
                }
            }
        } else if (state.screen == Screen.Settings) {
            IconButton(onClick = onCloseSettings) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Brass
                )
            }
        }
    }
}

@Composable
private fun SetupScreen(
    state: UiState,
    onSetup: (String, String?) -> Unit,
    onRequestBiometricEnroll: (String) -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var pin by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var decoyEnabled by remember { mutableStateOf(false) }
    var decoyPin by remember { mutableStateOf("") }
    var enableBiometric by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Text("Create your vault", style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("Primary PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it },
                label = { Text("Confirm PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false
                )
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = decoyEnabled, onCheckedChange = { decoyEnabled = it })
                Text("Enable decoy vault", color = onSurface)
            }
            if (decoyEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = decoyPin,
                    onValueChange = { decoyPin = it },
                    label = { Text("Decoy PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = enableBiometric, onCheckedChange = { enableBiometric = it })
                Text("Enable fingerprint unlock", color = onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
            val pinMismatch = pin.isNotEmpty() && confirm.isNotEmpty() && pin != confirm
            if (pinMismatch) {
                Text("PINs do not match", color = Ember, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    onSetup(pin, if (decoyEnabled) decoyPin else null)
                    if (enableBiometric) {
                        onRequestBiometricEnroll(pin)
                    }
                },
                enabled = !state.isBusy && pin.isNotBlank() && !pinMismatch,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("CREATE VAULT")
            }
        }
    }
}

@Composable
private fun UnlockScreen(
    state: UiState,
    onUnlock: (String) -> Unit,
    onRequestBiometricUnlock: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var pin by remember { mutableStateOf("") }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Text("Unlock", style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text("PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onUnlock(pin) },
                enabled = !state.isBusy && pin.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("UNLOCK")
            }
            if (state.biometricEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onRequestBiometricUnlock, enabled = !state.isBusy) {
                    Text("UNLOCK WITH FINGERPRINT", color = onSurface)
                }
            }
        }
    }
}

@Composable
private fun VaultScreen(
    state: UiState,
    onOpenNew: () -> Unit,
    onOpenNote: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var pendingReadOnce by remember { mutableStateOf<Note?>(null) }
    BoxWithConstraints {
        val compact = maxWidth < 360.dp
        val badgeSpacing = if (compact) 8.dp else 10.dp
        val sectionGap = if (compact) 10.dp else 12.dp
        val listTopPadding = if (compact) 20.dp else 28.dp
        val onSurface = MaterialTheme.colorScheme.onSurface
        val filteredNotes = state.notes
            .filter { note ->
                note.matchesQuery(state.searchQuery) &&
                    (state.activeLabel == null || note.labels.contains(state.activeLabel))
            }
            .sortedByDescending { it.createdAt }
        val (pinnedNotes, otherNotes) = filteredNotes.partition { it.pinned }

        Column {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChange,
                onCreate = onOpenNew
            )

            Spacer(modifier = Modifier.height(sectionGap))
            val nextExpiry = filteredNotes.mapNotNull { it.expiresAt }.minOrNull()
            if (compact) {
                Column(verticalArrangement = Arrangement.spacedBy(badgeSpacing)) {
                    VaultBadge(text = "Notes: ${filteredNotes.size}", tint = Sand)
                    if (nextExpiry != null) {
                        VaultBadge(text = formatExpiryBadge(nextExpiry), tint = Brass)
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(badgeSpacing)) {
                    VaultBadge(text = "Notes: ${filteredNotes.size}", tint = Sand)
                    if (nextExpiry != null) {
                        VaultBadge(text = formatExpiryBadge(nextExpiry), tint = Brass)
                    }
                }
            }

            Spacer(modifier = Modifier.height(sectionGap))
            if (state.notes.isEmpty()) {
                EmptyVaultState(onOpenNew)
                return@BoxWithConstraints
            }
            if (filteredNotes.isEmpty()) {
                EmptySearchState()
                return@BoxWithConstraints
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = listTopPadding, bottom = 16.dp)
            ) {
                if (pinnedNotes.isNotEmpty()) {
                    item(key = "pinned_header") {
                        SectionLabel("Pinned")
                    }
                    items(pinnedNotes, key = { it.id }) { note ->
                        NoteCard(note, onTogglePinned = onTogglePinned) {
                            if (note.readOnce) {
                                pendingReadOnce = note
                            } else {
                                onOpenNote(note.id)
                            }
                        }
                    }
                }
                if (otherNotes.isNotEmpty()) {
                    item(key = "others_header") {
                        SectionLabel("Others")
                    }
                }
                items(otherNotes, key = { it.id }) { note ->
                    NoteCard(note, onTogglePinned = onTogglePinned) {
                        if (note.readOnce) {
                            pendingReadOnce = note
                        } else {
                            onOpenNote(note.id)
                        }
                    }
                }
            }
        }
    }
    if (pendingReadOnce != null) {
        AlertDialog(
            onDismissRequest = { pendingReadOnce = null },
            title = { Text("Read-once note", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Opening this note will destroy it after reading.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = pendingReadOnce?.id
                        pendingReadOnce = null
                        if (id != null) onOpenNote(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                ) {
                    Text("OPEN")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingReadOnce = null }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun SettingsScreen(
    state: UiState,
    onUpdateDecoyPin: (String, String) -> Unit,
    onDisableDecoy: () -> Unit,
    onUpdateLockTimeout: (Long) -> Unit,
    onUpdateDefaultExpiry: (String) -> Unit,
    onUpdateDefaultReadOnce: (Boolean) -> Unit,
    onDisableBiometric: () -> Unit,
    onRequestBiometricEnroll: (String) -> Unit,
    onChangeRealPin: (String, String, String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onClose: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var decoyPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    val pinMismatch = decoyPin.isNotEmpty() && confirmPin.isNotEmpty() && decoyPin != confirmPin
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }
    var biometricPin by remember { mutableStateOf("") }
    val realPinMismatch = newPin.isNotEmpty() && confirmNewPin.isNotEmpty() && newPin != confirmNewPin
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Text("Settings", style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Auto-lock",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            val timeoutOptions = listOf(
                "Off" to 0L,
                "30s" to 30_000L,
                "1m" to 60_000L,
                "5m" to 300_000L,
                "10m" to 600_000L
            )
            val timeoutScroll = rememberScrollState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(timeoutScroll)
            ) {
                timeoutOptions.forEach { (label, value) ->
                    Chip(label, state.lockTimeoutMs == value) { onUpdateLockTimeout(value) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Default self-destruct",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            val expiryScroll = rememberScrollState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(expiryScroll)
            ) {
                Chip("None", state.defaultExpiry == "none") { onUpdateDefaultExpiry("none") }
                Chip("1h", state.defaultExpiry == "1h") { onUpdateDefaultExpiry("1h") }
                Chip("24h", state.defaultExpiry == "24h") { onUpdateDefaultExpiry("24h") }
                Chip("7d", state.defaultExpiry == "7d") { onUpdateDefaultExpiry("7d") }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.defaultReadOnce,
                    onCheckedChange = onUpdateDefaultReadOnce
                )
                Text("Read-once by default", color = onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("System", state.themeMode == ThemeMode.SYSTEM) { onUpdateThemeMode(ThemeMode.SYSTEM) }
                Chip("Dark", state.themeMode == ThemeMode.DARK) { onUpdateThemeMode(ThemeMode.DARK) }
                Chip("Light", state.themeMode == ThemeMode.LIGHT) { onUpdateThemeMode(ThemeMode.LIGHT) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Biometric unlock",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            val biometricStatus = if (state.biometricEnabled) "Enabled" else "Disabled"
            Text(
                text = "Status: $biometricStatus",
                color = onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
            if (state.biometricEnabled) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = onDisableBiometric, enabled = !state.isBusy) {
                    Text("DISABLE FINGERPRINT", color = Ember)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = biometricPin,
                    onValueChange = { biometricPin = it },
                    label = { Text("Current PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onRequestBiometricEnroll(biometricPin)
                        biometricPin = ""
                    },
                    enabled = !state.isBusy && biometricPin.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                ) {
                    Text("ENABLE FINGERPRINT")
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Primary PIN",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Changing the primary PIN rekeys and re-encrypts your vault.",
                color = onSurface.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge
            )
            OutlinedTextField(
                value = currentPin,
                onValueChange = { currentPin = it },
                label = { Text("Current PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = newPin,
                onValueChange = { newPin = it },
                label = { Text("New PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirmNewPin,
                onValueChange = { confirmNewPin = it },
                label = { Text("Confirm new PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false
                )
            )
            if (realPinMismatch) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("New PINs do not match", color = Ember, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    onChangeRealPin(currentPin, newPin, confirmNewPin)
                    currentPin = ""
                    newPin = ""
                    confirmNewPin = ""
                },
                enabled = !state.isBusy && currentPin.isNotBlank() && newPin.isNotBlank() && !realPinMismatch,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("CHANGE PIN")
            }
            if (state.isBusy) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rekeying vault...",
                    color = onSurface.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Decoy vault",
                style = MaterialTheme.typography.titleMedium,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            val decoyStatus = if (state.isDecoyEnabled) "Enabled" else "Disabled"
            Text(
                text = "Status: $decoyStatus",
                color = onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
            if (state.isDecoyEnabled) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Changing the decoy PIN resets the decoy vault.",
                    color = onSurface.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = decoyPin,
                onValueChange = { decoyPin = it },
                label = { Text(if (state.isDecoyEnabled) "New decoy PIN" else "Decoy PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { confirmPin = it },
                label = { Text("Confirm decoy PIN") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false
                )
            )
            if (pinMismatch) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Decoy PINs do not match", color = Ember, style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        onUpdateDecoyPin(decoyPin, confirmPin)
                        decoyPin = ""
                        confirmPin = ""
                    },
                    enabled = !state.isBusy && decoyPin.isNotBlank() && !pinMismatch,
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                ) {
                    Text(if (state.isDecoyEnabled) "CHANGE PIN" else "ENABLE")
                }
                if (state.isDecoyEnabled) {
                    TextButton(onClick = onDisableDecoy, enabled = !state.isBusy) {
                        Text("DISABLE", color = Ember)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onClose) {
                Text("BACK", color = Brass)
            }
        }
    }
}

@Composable
private fun VaultBadge(text: String, tint: Color) {
    Box(
        modifier = Modifier
            .background(tint.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = tint, style = MaterialTheme.typography.labelLarge)
    }
}

private fun formatExpiryBadge(expiresAt: Long): String {
    val remaining = expiresAt - System.currentTimeMillis()
    if (remaining <= 0L) return "Next expiry: overdue"
    val minutes = max(1, remaining / 60_000L)
    val hours = remaining / 3_600_000L
    val days = remaining / 86_400_000L
    return when {
        days >= 1 -> "Next expiry: ${days}d"
        hours >= 1 -> "Next expiry: ${hours}h"
        else -> "Next expiry: ${minutes}m"
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCreate: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search notes", color = onSurface.copy(alpha = 0.6f)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            )
        )
        IconButton(onClick = onCreate) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New note",
                tint = onSurface
            )
        }
    }
}

@Composable
private fun LabelFilters(
    labels: List<String>,
    activeLabel: String?,
    onSelectLabel: (String?) -> Unit
) {
    val scroll = rememberScrollState()
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(scroll)
    ) {
        Chip("All", activeLabel == null) { onSelectLabel(null) }
        labels.forEach { label ->
            Chip(label, activeLabel == label) { onSelectLabel(label) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun NoteCard(note: Note, onTogglePinned: (String) -> Unit, onOpen: () -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val preview = note.text.trim().replace("\n", " ")
    val checklistPreview = note.checklist.take(3)
    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val previewText = when {
                    preview.isNotBlank() -> preview
                    checklistPreview.isNotEmpty() -> "Checklist note"
                    note.attachments.isNotEmpty() -> "Image note"
                    else -> "Empty note"
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
                        contentDescription = if (note.pinned) "Unpin" else "Pin",
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
            if (note.attachments.isNotEmpty()) {
                Text(
                    text = "${note.attachments.size} image(s)",
                    style = MaterialTheme.typography.labelLarge,
                    color = onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (note.readOnce) {
                    Text(
                        text = "READ ONCE",
                        style = MaterialTheme.typography.labelLarge,
                        color = Brass
                    )
                }
                if (note.expiresAt != null) {
                    Text(
                        text = "EXPIRING",
                        style = MaterialTheme.typography.labelLarge,
                        color = Ember
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelPill(text: String) {
    Box(
        modifier = Modifier
            .background(Moss.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Moss, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun RemovableLabelPill(text: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Moss.copy(alpha = 0.22f), RoundedCornerShape(10.dp))
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text(text = text, color = Moss, style = MaterialTheme.typography.labelLarge)
        IconButton(onClick = onRemove, modifier = Modifier.height(20.dp)) {
            Text("X", color = Moss, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun EmptySearchState() {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "No matches",
                color = onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Try a different search or label filter.",
                color = onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun resolveDisplayName(context: android.content.Context, uri: Uri): String? {
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
@Composable
private fun EmptyVaultState(onOpenNew: () -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(onSurface.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your vault is empty.",
                color = onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Create your first note. Read-once and expiry are available.",
                color = onSurface.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenNew,
                colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
            ) {
                Text("NEW NOTE")
            }
        }
    }
}

@Composable
private fun NewNoteScreen(
    state: UiState,
    onCreate: (String, List<ChecklistItem>, List<String>, Boolean, List<Uri>, Long?, Boolean) -> Unit,
    defaultExpiry: String,
    defaultReadOnce: Boolean
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var content by remember { mutableStateOf("") }
    var readOnce by remember(defaultReadOnce) { mutableStateOf(defaultReadOnce) }
    var expiryChoice by remember(defaultExpiry) { mutableStateOf(defaultExpiry) }
    var pinned by remember { mutableStateOf(false) }
    var showChecklist by remember { mutableStateOf(false) }
    var showAttachments by remember { mutableStateOf(false) }
    var showAddMenu by remember { mutableStateOf(false) }
    var checklistItems by remember { mutableStateOf(listOf<ChecklistItem>()) }
    var newChecklistItem by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(listOf<Uri>()) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            attachments = attachments + uri
        }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Text("New note", style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { showAddMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Text("ADD")
                }
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Checklist") },
                        onClick = {
                            showChecklist = true
                            showAddMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Image") },
                        onClick = {
                            showAttachments = true
                            showAddMenu = false
                        }
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                if (showChecklist) {
                    Text("Checklist enabled", color = onSurface.copy(alpha = 0.7f))
                } else if (showAttachments) {
                    Text("Image enabled", color = onSurface.copy(alpha = 0.7f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write safely...") },
                minLines = 6
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pinned, onCheckedChange = { pinned = it })
                Text("Pin to top", color = onSurface)
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (showChecklist) {
                Text("Checklist", style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newChecklistItem,
                        onValueChange = { newChecklistItem = it },
                        label = { Text("Add item") },
                        modifier = Modifier.weight(1f),
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
                            }
                        },
                        enabled = newChecklistItem.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text("ADD")
                    }
                }
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
                                Text("REMOVE", color = Ember)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (showAttachments) {
                Text("Attachments", style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { imagePicker.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text("ADD IMAGE")
                    }
                    if (attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("${attachments.size} attached", color = onSurface.copy(alpha = 0.7f))
                    }
                }
                if (attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    attachments.forEach { uri ->
                        val name = resolveDisplayName(context, uri) ?: "image"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(name, color = onSurface, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                attachments = attachments.filterNot { it == uri }
                            }) {
                                Text("REMOVE", color = Ember)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = readOnce, onCheckedChange = { readOnce = it })
                Text("Read once", color = onSurface)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Expiry", style = MaterialTheme.typography.labelLarge, color = onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("None", expiryChoice == "none") { expiryChoice = "none" }
                Chip("1h", expiryChoice == "1h") { expiryChoice = "1h" }
                Chip("24h", expiryChoice == "24h") { expiryChoice = "24h" }
                Chip("7d", expiryChoice == "7d") { expiryChoice = "7d" }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val expiresAt = when (expiryChoice) {
                "1h" -> System.currentTimeMillis() + 3_600_000L
                "24h" -> System.currentTimeMillis() + 86_400_000L
                "7d" -> System.currentTimeMillis() + 604_800_000L
                else -> null
            }
            Button(
                onClick = { onCreate(content, checklistItems, emptyList(), pinned, attachments, expiresAt, readOnce) },
                enabled = !state.isBusy,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("SAVE NOTE")
            }
        }
    }
}

@Composable
private fun NoteDetailScreen(
    state: UiState,
    onClose: () -> Unit,
    onDelete: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleChecklistItem: (String, String) -> Unit,
    onAddChecklistItem: (String, String) -> Unit,
    onRemoveChecklistItem: (String, String) -> Unit,
    onUpdateChecklistText: (String, String, String) -> Unit,
    onMoveChecklistItem: (String, String, Int) -> Unit,
    onLoadAttachmentPreview: (String, String) -> Unit,
    onRemoveAttachment: (String, String) -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val note = state.selectedNote
    if (note == null) {
        Text("Note unavailable", color = onSurface)
        return
    }
    var checklistInput by remember { mutableStateOf("") }
    var editingChecklistId by remember { mutableStateOf<String?>(null) }
    var editingChecklistText by remember { mutableStateOf("") }
    val checklistBounds = remember { mutableStateMapOf<String, IntRange>() }
    var draggingChecklistId by remember { mutableStateOf<String?>(null) }
    var dragY by remember { mutableStateOf(0f) }
    var lastSwapTargetId by remember { mutableStateOf<String?>(null) }
    var dragTargetId by remember { mutableStateOf<String?>(null) }
    val haptics = LocalHapticFeedback.current
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Note",
                    style = MaterialTheme.typography.titleLarge,
                    color = onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onTogglePinned(note.id) }) {
                    Icon(
                        imageVector = if (note.pinned) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (note.pinned) "Unpin" else "Pin",
                        tint = if (note.pinned) Brass else onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (note.text.isNotBlank()) {
                Text(note.text, color = onSurface, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (note.checklist.isNotEmpty()) {
                Text("Checklist", color = onSurface, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
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
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DragHandle,
                            contentDescription = "Reorder",
                            tint = onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 2.dp, end = 6.dp)
                        )
                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = { onToggleChecklistItem(note.id, item.id) }
                        )
                        if (editingChecklistId == item.id) {
                            OutlinedTextField(
                                value = editingChecklistText,
                                onValueChange = { editingChecklistText = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                onUpdateChecklistText(note.id, item.id, editingChecklistText)
                                editingChecklistId = null
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Save item",
                                    tint = Brass
                                )
                            }
                            IconButton(onClick = { editingChecklistId = null }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Cancel edit",
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
                                    contentDescription = "Edit item",
                                    tint = Brass
                                )
                            }
                        }
                        IconButton(onClick = { onMoveChecklistItem(note.id, item.id, -1) }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowUpward,
                                contentDescription = "Move up",
                                tint = Sand
                            )
                        }
                        IconButton(onClick = { onMoveChecklistItem(note.id, item.id, 1) }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDownward,
                                contentDescription = "Move down",
                                tint = Sand
                            )
                        }
                        IconButton(onClick = { onRemoveChecklistItem(note.id, item.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remove item",
                                tint = Ember
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Text("Checklist", color = onSurface, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = checklistInput,
                    onValueChange = { checklistInput = it },
                    label = { Text("Add item") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        onAddChecklistItem(note.id, checklistInput)
                        checklistInput = ""
                    },
                    enabled = checklistInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Text("ADD")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (note.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Attachments", color = onSurface, style = MaterialTheme.typography.labelLarge)
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
                        TextButton(onClick = { onRemoveAttachment(note.id, attachment.id) }) {
                            Text("REMOVE", color = Ember)
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
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onDelete(note.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = Ember, contentColor = Sand)
                ) {
                    Text("DELETE")
                }
                TextButton(onClick = onClose) {
                    Text("BACK", color = Brass)
                }
            }
        }
    }
}

@Composable
private fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Brass else Coal
    val fg = if (selected) Ink else Sand
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(containerColor = bg, contentColor = fg),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ErrorBar(state: UiState, onClear: () -> Unit) {
    val msg = state.error ?: return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .alpha(0.95f)
            .background(Ember, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(msg, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onClear) {
                Text("OK", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LabelsMenu(
    state: UiState,
    onSelectLabel: (String?) -> Unit,
    onAddLabel: (String, String) -> Unit,
    onRemoveLabel: (String, String) -> Unit,
    onClose: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val labels = state.notes.flatMap { it.labels }.distinct().sorted()
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
                Text("Labels", style = MaterialTheme.typography.titleMedium, color = onSurface)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onClose) {
                    Text("HIDE", color = Brass)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Chip("All", state.activeLabel == null) { onSelectLabel(null) }
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
                                contentDescription = if (hasLabel) "Remove label" else "Add label",
                                tint = if (hasLabel) Ember else Moss
                            )
                        }
                    }
                }
            }
            if (selectedNote != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Assign label", style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = labelInput,
                        onValueChange = { labelInput = it },
                        label = { Text("New label") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onAddLabel(selectedNote.id, labelInput)
                            labelInput = ""
                        },
                        enabled = labelInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text("ADD")
                    }
                }
            }
        }
    }
}


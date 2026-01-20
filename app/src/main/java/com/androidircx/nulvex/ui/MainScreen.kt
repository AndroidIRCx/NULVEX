package com.androidircx.nulvex.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.androidircx.nulvex.data.Note
import com.androidircx.nulvex.ui.theme.Brass
import com.androidircx.nulvex.ui.theme.Coal
import com.androidircx.nulvex.ui.theme.Ember
import com.androidircx.nulvex.ui.theme.Ink
import com.androidircx.nulvex.ui.theme.Moss
import com.androidircx.nulvex.ui.theme.Sand
import com.androidircx.nulvex.ui.theme.ThemeMode
import kotlin.math.max

@Composable
fun MainScreen(state: UiState, onSetup: (String, String?) -> Unit, onUnlock: (String) -> Unit,
               onLock: () -> Unit, onPanic: () -> Unit, onOpenSettings: () -> Unit,
               onCloseSettings: () -> Unit, onUpdateDecoyPin: (String, String) -> Unit,
               onDisableDecoy: () -> Unit, onUpdateLockTimeout: (Long) -> Unit,
               onUpdateDefaultExpiry: (String) -> Unit, onUpdateDefaultReadOnce: (Boolean) -> Unit,
               onRequestBiometricEnroll: (String) -> Unit, onRequestBiometricUnlock: () -> Unit,
               onDisableBiometric: () -> Unit, onChangeRealPin: (String, String, String) -> Unit,
               onUpdateThemeMode: (ThemeMode) -> Unit,
               onOpenNew: () -> Unit, onCreate: (String, Long?, Boolean) -> Unit,
               onOpenNote: (String) -> Unit, onCloseNote: () -> Unit, onDelete: (String) -> Unit,
               onClearError: () -> Unit) {
    var showPanicConfirm by remember { mutableStateOf(false) }
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            TopHeader(
                state = state,
                onLock = onLock,
                onPanicClick = { showPanicConfirm = true },
                onOpenSettings = onOpenSettings,
                onCloseSettings = onCloseSettings
            )
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350)) { 40 }
            ) {
                when (state.screen) {
                    Screen.Setup -> SetupScreen(state, onSetup, onRequestBiometricEnroll)
                    Screen.Unlock -> UnlockScreen(state, onUnlock, onRequestBiometricUnlock)
                    Screen.Vault -> VaultScreen(state, onOpenNew, onOpenNote)
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
                    Screen.NoteDetail -> NoteDetailScreen(state, onCloseNote, onDelete)
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
    onCloseSettings: () -> Unit
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
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = onBackground.copy(alpha = 0.7f)
                    )
                }
                TextButton(onClick = onPanicClick) {
                    Text(text = "PANIC", color = Ember)
                }
                TextButton(onClick = onLock) {
                    Text(text = "LOCK", color = Brass)
                }
            }
        } else if (state.screen == Screen.Settings) {
            TextButton(onClick = onCloseSettings) {
                Text(text = "BACK", color = Brass)
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
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
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
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
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
private fun VaultScreen(state: UiState, onOpenNew: () -> Unit, onOpenNote: (String) -> Unit) {
    var pendingReadOnce by remember { mutableStateOf<Note?>(null) }
    BoxWithConstraints {
        val compact = maxWidth < 360.dp
        val badgeSpacing = if (compact) 8.dp else 10.dp
        val sectionGap = if (compact) 10.dp else 12.dp
        val listTopPadding = if (compact) 20.dp else 28.dp
        val onSurface = MaterialTheme.colorScheme.onSurface

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your vault", style = MaterialTheme.typography.titleLarge, color = onSurface)
                Button(
                    onClick = onOpenNew,
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                ) {
                    Text("NEW NOTE")
                }
            }

            Spacer(modifier = Modifier.height(sectionGap))
            HorizontalDivider(color = onSurface.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(sectionGap))

            val nextExpiry = state.notes.mapNotNull { it.expiresAt }.minOrNull()
            if (compact) {
                Column(verticalArrangement = Arrangement.spacedBy(badgeSpacing)) {
                    VaultBadge(text = "Notes: ${state.notes.size}", tint = Sand)
                    if (nextExpiry != null) {
                        VaultBadge(text = formatExpiryBadge(nextExpiry), tint = Brass)
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(badgeSpacing)) {
                    VaultBadge(text = "Notes: ${state.notes.size}", tint = Sand)
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = listTopPadding, bottom = 16.dp)
            ) {
                items(state.notes, key = { it.id }) { note ->
                    NoteCard(note) {
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
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
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
private fun NoteCard(note: Note, onOpen: () -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val preview = note.content.trim().replace("\n", " ")
    Card(
        onClick = onOpen,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = preview.ifBlank { "Empty note" },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
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
    onCreate: (String, Long?, Boolean) -> Unit,
    defaultExpiry: String,
    defaultReadOnce: Boolean
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var content by remember { mutableStateOf("") }
    var readOnce by remember(defaultReadOnce) { mutableStateOf(defaultReadOnce) }
    var expiryChoice by remember(defaultExpiry) { mutableStateOf(defaultExpiry) }

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
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Write safely...") },
                minLines = 6
            )
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
                onClick = { onCreate(content, expiresAt, readOnce) },
                enabled = !state.isBusy,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text("SAVE NOTE")
            }
        }
    }
}

@Composable
private fun NoteDetailScreen(state: UiState, onClose: () -> Unit, onDelete: (String) -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val note = state.selectedNote
    if (note == null) {
        Text("Note unavailable", color = onSurface)
        return
    }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Note", style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(10.dp))
            Text(note.content, color = onSurface, style = MaterialTheme.typography.bodyLarge)
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


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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
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
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    state: UiState,
    onCompleteOnboarding: () -> Unit,
    onSetup: (String, String?, Boolean) -> Unit,
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
    onUpdateNoteText: (String, String) -> Unit,
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
    onClearError: () -> Unit,
    onWatchAdToRemoveAds: () -> Unit = {},
    onWatchAdForShares: () -> Unit = {}
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
            ) {
                if (!state.isAdFree) {
                    BannerAdSection(
                        adUnitId = AdManager.AD_UNIT_BANNER,
                        onRemoveAds = onWatchAdToRemoveAds
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
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
                        Screen.Onboarding -> OnboardingScreen(onComplete = onCompleteOnboarding)
                        Screen.Setup -> SetupScreen(state, onSetup)
                        Screen.Unlock -> UnlockScreen(state, onUnlock, onRequestBiometricUnlock)
                        Screen.Vault -> VaultScreen(
                            state = state,
                            onOpenNew = onOpenNew,
                            onOpenNote = onOpenNote,
                            onTogglePinned = onTogglePinned,
                            onDelete = onDelete,
                            onSearchQueryChange = onSearchQueryChange
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
                            onClose = onCloseSettings,
                            onWatchAdToRemoveAds = onWatchAdToRemoveAds,
                            onWatchAdForShares = onWatchAdForShares
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
                            onUpdateNoteText,
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
                } // end inner padding Column
            }
        }
        ErrorBar(state, onClearError)
        if (showPanicConfirm) {
            var holdProgress by remember { mutableFloatStateOf(0f) }
            var isHolding by remember { mutableStateOf(false) }
            val haptic = LocalHapticFeedback.current
            LaunchedEffect(isHolding) {
                if (isHolding) {
                    val totalSteps = 156L // ~2500ms at 16ms intervals
                    var step = 0L
                    while (isHolding && step < totalSteps) {
                        delay(16L)
                        step++
                        holdProgress = step.toFloat() / totalSteps.toFloat()
                    }
                    if (holdProgress >= 1f) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showPanicConfirm = false
                        holdProgress = 0f
                        onPanic()
                    }
                } else {
                    holdProgress = 0f
                }
            }
            Dialog(onDismissRequest = { showPanicConfirm = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Coal)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Ember,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "PANIC WIPE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Ember
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "All vault data will be permanently destroyed. This cannot be undone.",
                            color = Sand.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Ember.copy(alpha = 0.15f))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isHolding = true
                                            tryAwaitRelease()
                                            isHolding = false
                                        }
                                    )
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(holdProgress)
                                    .fillMaxHeight()
                                    .background(Ember.copy(alpha = 0.7f))
                            )
                            Text(
                                text = if (holdProgress > 0.01f) "WIPING..." else "HOLD TO WIPE",
                                color = Sand,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = { showPanicConfirm = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CANCEL", color = Sand.copy(alpha = 0.7f))
                        }
                    }
                }
            }
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

/**
 * Full-width banner ad row with a small "Remove ads (10 min)" text button below it.
 * Only rendered when [state.isAdFree] is false.
 */
@Composable
private fun BannerAdSection(adUnitId: String, onRemoveAds: () -> Unit) {
    Column {
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    val density = ctx.resources.displayMetrics.density
                    val widthPx = ctx.resources.displayMetrics.widthPixels.toFloat()
                    val adWidthDp = (widthPx / density).toInt()
                    setAdSize(
                        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, adWidthDp)
                    )
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onRemoveAds) {
                Text(
                    text = "Remove ads (10 min)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        HorizontalDivider()
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
private fun PinKey(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(Sand.copy(alpha = 0.08f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium,
            color = Sand,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun SecurePinPad(
    pin: String,
    label: String,
    onPinChange: (String) -> Unit,
    maxLength: Int = 12
) {
    val haptic = LocalHapticFeedback.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Sand.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(24.dp)
        ) {
            if (pin.isEmpty()) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(12.dp)
                            .border(1.5.dp, Sand.copy(alpha = 0.35f), CircleShape)
                    )
                }
            } else {
                repeat(pin.length) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(12.dp)
                            .background(Sand, CircleShape)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "⌫")
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    row.forEach { key ->
                        when (key) {
                            "" -> Spacer(modifier = Modifier.size(72.dp))
                            "⌫" -> PinKey(key) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (pin.isNotEmpty()) onPinChange(pin.dropLast(1))
                            }
                            else -> PinKey(key) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (pin.length < maxLength) onPinChange(pin + key)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupScreen(
    state: UiState,
    onSetup: (String, String?, Boolean) -> Unit
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
                    onSetup(pin, if (decoyEnabled) decoyPin else null, enableBiometric)
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
    var pin by remember { mutableStateOf("") }
    var lockoutRemainingSecs by remember { mutableStateOf(0L) }
    val isLockedOut = lockoutRemainingSecs > 0L

    LaunchedEffect(state.lockoutUntil) {
        while (true) {
            val remaining = (state.lockoutUntil - System.currentTimeMillis()) / 1000L
            lockoutRemainingSecs = maxOf(0L, remaining)
            if (lockoutRemainingSecs == 0L) break
            delay(1000L)
        }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Unlock", style = MaterialTheme.typography.titleLarge, color = Sand)
            Spacer(modifier = Modifier.height(28.dp))
            SecurePinPad(
                pin = pin,
                label = "Enter PIN",
                onPinChange = { if (!isLockedOut) pin = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (isLockedOut) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Ember.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Too many attempts. Try again in ${lockoutRemainingSecs}s",
                        color = Ember,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Button(
                    onClick = { onUnlock(pin) },
                    enabled = !state.isBusy && pin.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                ) {
                    Text("UNLOCK")
                }
            }
            if (state.biometricEnabled && !isLockedOut) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = onRequestBiometricUnlock,
                    enabled = !state.isBusy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("UNLOCK WITH FINGERPRINT", color = Sand.copy(alpha = 0.7f))
                }
            }
        }
    }
}

private enum class SortMode(val label: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    EXPIRING("Expiring soon")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VaultScreen(
    state: UiState,
    onOpenNew: () -> Unit,
    onOpenNote: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onDelete: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var pendingReadOnce by remember { mutableStateOf<Note?>(null) }
    var pendingDelete by remember { mutableStateOf<Note?>(null) }
    var sortMode by remember { mutableStateOf(SortMode.NEWEST) }
    var showSortMenu by remember { mutableStateOf(false) }

    BoxWithConstraints {
        val compact = maxWidth < 360.dp
        val badgeSpacing = if (compact) 8.dp else 10.dp
        val sectionGap = if (compact) 10.dp else 12.dp
        val listTopPadding = if (compact) 16.dp else 20.dp
        val onSurface = MaterialTheme.colorScheme.onSurface

        val filteredNotes = state.notes
            .filter { note ->
                note.matchesQuery(state.searchQuery) &&
                    (state.activeLabel == null || note.labels.contains(state.activeLabel))
            }
            .let { notes ->
                when (sortMode) {
                    SortMode.NEWEST -> notes.sortedByDescending { it.createdAt }
                    SortMode.OLDEST -> notes.sortedBy { it.createdAt }
                    SortMode.EXPIRING -> notes.sortedBy { it.expiresAt ?: Long.MAX_VALUE }
                }
            }
        val (pinnedNotes, otherNotes) = filteredNotes.partition { it.pinned }

        // Statistics
        val readOnceCount = filteredNotes.count { it.readOnce }
        val expiringCount = filteredNotes.count { it.expiresAt != null }
        val nextExpiry = filteredNotes.mapNotNull { it.expiresAt }.minOrNull()

        Column {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = onSearchQueryChange,
                onCreate = onOpenNew
            )

            Spacer(modifier = Modifier.height(sectionGap))

            // Stats and sort row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(badgeSpacing)) {
                    VaultBadge(text = "${filteredNotes.size} notes", tint = Sand)
                    if (readOnceCount > 0) {
                        VaultBadge(text = "$readOnceCount burn", tint = Brass)
                    }
                    if (expiringCount > 0) {
                        VaultBadge(text = "$expiringCount expiring", tint = Ember)
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
                            contentDescription = "Sort",
                            tint = onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.height(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            sortMode.label,
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
                                        mode.label,
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
                        SwipeableNoteCard(
                            note = note,
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
                if (otherNotes.isNotEmpty()) {
                    item(key = "others_header") {
                        SectionLabel(if (pinnedNotes.isEmpty()) "Notes" else "Others")
                    }
                }
                items(otherNotes, key = { it.id }) { note ->
                    SwipeableNoteCard(
                        note = note,
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
                    Text(
                        "BURN NOTE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Brass
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "This note is set to read-once. It will be permanently destroyed after you close it.",
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
                        Text("OPEN & BURN", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { pendingReadOnce = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CANCEL", color = Sand.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete note?", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "This action cannot be undone.",
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
                    Text("DELETE")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun SwipeableNoteCard(
    note: Note,
    onTogglePinned: (String) -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false // Don't actually dismiss, show confirmation dialog
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onTogglePinned(note.id)
                    false // Reset after action
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

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
        NoteCard(note = note, onTogglePinned = onTogglePinned, onOpen = onOpen)
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
    onClose: () -> Unit,
    onWatchAdToRemoveAds: () -> Unit = {},
    onWatchAdForShares: () -> Unit = {}
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
    var settingsSearch by remember { mutableStateOf("") }
    var expandedSections by remember { mutableStateOf(setOf<String>()) }
    val normalizedQuery = settingsSearch.trim().lowercase()

    fun matchesSection(vararg terms: String): Boolean {
        if (normalizedQuery.isBlank()) return true
        return terms.any { it.lowercase().contains(normalizedQuery) }
    }

    fun isExpanded(sectionId: String): Boolean {
        return normalizedQuery.isNotBlank() || expandedSections.contains(sectionId)
    }

    fun toggleSection(sectionId: String) {
        expandedSections = if (expandedSections.contains(sectionId)) {
            expandedSections - sectionId
        } else {
            expandedSections + sectionId
        }
    }

    val showAds = matchesSection(
        "rewards",
        "ads",
        "credits",
        "share credits",
        "watch ad",
        "remove ads",
        "ad-free time"
    )
    val showDisplay = matchesSection("display", "theme", "appearance", "dark", "light", "system")
    val showVaultDefaults = matchesSection(
        "vault defaults",
        "auto-lock",
        "timeout",
        "self-destruct",
        "expiry",
        "read-once"
    )
    val showSecurity = matchesSection(
        "security",
        "fingerprint",
        "biometric",
        "pin",
        "change primary pin",
        "encryption"
    )
    val showDanger = matchesSection(
        "danger zone",
        "decoy",
        "decoy pin",
        "wipe",
        "coercion",
        "plausible deniability"
    )
    val showAbout = matchesSection("about", "version", "nulvex", "offline", "xchacha20", "kyber768")
    val hasVisibleSections = showAds || showDisplay || showVaultDefaults || showSecurity || showDanger || showAbout

    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            nowMs = System.currentTimeMillis()
        }
    }

    val remainingMs = maxOf(0L, state.adFreeUntil - nowMs)
    val adFreeActive = remainingMs > 0L

    fun formatRemaining(ms: Long): String {
        val totalSecs = ms / 1000L
        val mins = totalSecs / 60L
        val secs = totalSecs % 60L
        return if (mins > 0L) "${mins}m ${secs}s" else "${secs}s"
    }

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
            OutlinedTextField(
                value = settingsSearch,
                onValueChange = { settingsSearch = it },
                label = { Text("Search settings") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (settingsSearch.isNotBlank()) {
                        IconButton(onClick = { settingsSearch = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (showAds) {
                SettingsSection(
                    icon = Icons.Filled.Star,
                    title = "Rewards & Ads",
                    description = "Remove ads and earn share credits",
                    expanded = isExpanded("ads"),
                    onToggle = { toggleSection("ads") }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = null,
                            tint = if (adFreeActive) Moss else onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ad-free time", color = onSurface)
                            Text(
                                if (adFreeActive) "${formatRemaining(remainingMs)} remaining" else "Ads are active",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (adFreeActive) Moss else onSurface.copy(alpha = 0.6f)
                            )
                        }
                        if (adFreeActive) {
                            Box(
                                modifier = Modifier
                                    .background(Moss.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("ACTIVE", style = MaterialTheme.typography.labelSmall, color = Moss)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onWatchAdToRemoveAds,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (adFreeActive) "EXTEND BY 10 MIN" else "WATCH AD - 10 MIN NO ADS")
                    }
                    Text(
                        "Stacks - watch multiple times to bank more ad-free minutes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = onSurface.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = null,
                            tint = Brass,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Share credits", color = onSurface)
                            Text(
                                "Used to share notes via the secure API",
                                style = MaterialTheme.typography.bodySmall,
                                color = onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Brass.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "${state.shareCredits}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Brass
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onWatchAdForShares,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("WATCH AD - EARN 1 SHARE CREDIT")
                    }
                    Text(
                        "Credits accumulate - watch 3 ads to earn 3 shares.",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            if (showDisplay) {
                if (showAds) SettingsDivider()
                SettingsSection(
                    icon = Icons.Filled.Palette,
                    title = "Display",
                    description = "Appearance and theme",
                    expanded = isExpanded("display"),
                    onToggle = { toggleSection("display") }
                ) {
                    Text("Theme", style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip("System", state.themeMode == ThemeMode.SYSTEM) { onUpdateThemeMode(ThemeMode.SYSTEM) }
                        Chip("Dark", state.themeMode == ThemeMode.DARK) { onUpdateThemeMode(ThemeMode.DARK) }
                        Chip("Light", state.themeMode == ThemeMode.LIGHT) { onUpdateThemeMode(ThemeMode.LIGHT) }
                    }
                }
            }

            if (showVaultDefaults && (showAds || showDisplay)) SettingsDivider()

            // === VAULT DEFAULTS SECTION ===
            if (showVaultDefaults) SettingsSection(
                icon = Icons.Filled.Timer,
                title = "Vault defaults",
                description = "Auto-lock and note behavior",
                expanded = isExpanded("vault_defaults"),
                onToggle = { toggleSection("vault_defaults") }
            ) {
                Text("Auto-lock timeout", style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(8.dp))
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
                Text("Default self-destruct", style = MaterialTheme.typography.labelLarge, color = onSurface)
                Text(
                    "New notes will use this expiry setting",
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.defaultReadOnce,
                        onCheckedChange = onUpdateDefaultReadOnce
                    )
                    Column {
                        Text("Read-once by default", color = onSurface)
                        Text(
                            "Notes are deleted after first read",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            if (showSecurity && (showAds || showDisplay || showVaultDefaults)) SettingsDivider()

            // === SECURITY SECTION ===
            if (showSecurity) SettingsSection(
                icon = Icons.Filled.Security,
                title = "Security",
                description = "Authentication and encryption",
                expanded = isExpanded("security"),
                onToggle = { toggleSection("security") }
            ) {
                // Biometric
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = null,
                        tint = if (state.biometricEnabled) Moss else onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fingerprint unlock", color = onSurface)
                        Text(
                            if (state.biometricEnabled) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.biometricEnabled) Moss else onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (state.biometricEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDisableBiometric, enabled = !state.isBusy) {
                        Text("DISABLE FINGERPRINT", color = Ember)
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = biometricPin,
                        onValueChange = { biometricPin = it },
                        label = { Text("Current PIN to enable") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done,
                            autoCorrectEnabled = false
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // Change PIN
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Brass,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text("Change primary PIN", color = onSurface)
                        Text(
                            "Re-encrypts your entire vault",
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
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
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (realPinMismatch) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("PINs do not match", color = Ember, style = MaterialTheme.typography.labelMedium)
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
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Rekeying vault...", color = Brass, style = MaterialTheme.typography.labelMedium)
                }
            }

            if (showDanger && (showAds || showDisplay || showVaultDefaults || showSecurity)) SettingsDivider()

            // === DANGER ZONE SECTION ===
            if (showDanger) SettingsSection(
                icon = Icons.Filled.VisibilityOff,
                title = "Danger zone",
                description = "Decoy vault and destructive actions",
                accentColor = Ember,
                expanded = isExpanded("danger"),
                onToggle = { toggleSection("danger") }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Decoy vault", color = onSurface)
                        Text(
                            if (state.isDecoyEnabled) "Active - separate fake vault" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isDecoyEnabled) Brass else onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (state.isDecoyEnabled) {
                        Box(
                            modifier = Modifier
                                .background(Brass.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("ENABLED", style = MaterialTheme.typography.labelSmall, color = Brass)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "A decoy vault opens when you enter a different PIN. " +
                        "Use it for plausible deniability under coercion.",
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = decoyPin,
                    onValueChange = { decoyPin = it },
                    label = { Text(if (state.isDecoyEnabled) "New decoy PIN" else "Set decoy PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next,
                        autoCorrectEnabled = false
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (pinMismatch) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("PINs do not match", color = Ember, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(modifier = Modifier.height(10.dp))
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
                        Text(if (state.isDecoyEnabled) "CHANGE" else "ENABLE")
                    }
                    if (state.isDecoyEnabled) {
                        TextButton(onClick = onDisableDecoy, enabled = !state.isBusy) {
                            Text("DISABLE", color = Ember)
                        }
                    }
                }

                if (state.isDecoyEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Changing the decoy PIN wipes the old decoy vault.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ember.copy(alpha = 0.8f)
                    )
                }
            }

            if (showAbout && (showAds || showDisplay || showVaultDefaults || showSecurity || showDanger)) {
                SettingsDivider()
            }

            // === ABOUT SECTION ===
            if (showAbout) SettingsSection(
                icon = Icons.Filled.Info,
                title = "About",
                description = "App information",
                expanded = isExpanded("about"),
                onToggle = { toggleSection("about") }
            ) {
                Text("Nulvex", style = MaterialTheme.typography.titleMedium, color = onSurface)
                Text(
                    "Offline-first secure vault",
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Version ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurface.copy(alpha = 0.5f)
                    )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "XChaCha20-Poly1305 + Kyber768",
                    style = MaterialTheme.typography.labelSmall,
                    color = Moss.copy(alpha = 0.8f)
                )
            }

            if (!hasVisibleSections) {
                Text(
                    "No settings match your search.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("BACK TO VAULT", color = Brass)
            }
        }
    }
}

@Composable
private fun SettingsSection(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color = Brass,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = onSurface)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = onSurface.copy(alpha = 0.65f)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 36.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        thickness = 1.dp
    )
    Spacer(modifier = Modifier.height(20.dp))
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(onComplete: () -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.Shield,
            title = "Welcome to Nulvex",
            description = "Your offline-first secure vault. No cloud, no tracking, no compromise."
        ),
        OnboardingPage(
            icon = Icons.Filled.Security,
            title = "Military-grade encryption",
            description = "Your notes are encrypted with XChaCha20-Poly1305 and post-quantum ready key exchange."
        ),
        OnboardingPage(
            icon = Icons.Filled.Schedule,
            title = "Self-destruct notes",
            description = "Set notes to auto-delete after 1 hour, 24 hours, or 7 days. Or make them read-once."
        ),
        OnboardingPage(
            icon = Icons.Filled.Warning,
            title = "Panic button",
            description = "Instantly wipe everything if needed. Optional decoy vault for plausible deniability."
        ),
        OnboardingPage(
            icon = Icons.Filled.Fingerprint,
            title = "Biometric unlock",
            description = "Use your fingerprint to unlock quickly. Your PIN remains the master key."
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val item = pages[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = Brass,
                        modifier = Modifier
                            .height(80.dp)
                            .width(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(pages.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(if (selected) 24.dp else 8.dp)
                            .background(
                                if (selected) Brass else onSurface.copy(alpha = 0.3f),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Text("BACK", color = onSurface.copy(alpha = 0.7f))
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (pagerState.currentPage < pages.size - 1) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text("NEXT")
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text("GET STARTED")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = onComplete) {
                    Text("SKIP", color = onSurface.copy(alpha = 0.5f))
                }
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
                text = "Your vault is ready",
                color = onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first encrypted note",
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
                    text = "Set notes to self-destruct after 1h, 24h, or 7 days"
                )
                FeatureHint(
                    icon = Icons.Filled.Security,
                    text = "Read-once notes are deleted after viewing"
                )
                FeatureHint(
                    icon = Icons.Filled.Star,
                    text = "Swipe right to pin, swipe left to delete"
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
                Text("CREATE FIRST NOTE")
            }
        }
    }
}

@Composable
private fun FeatureHint(icon: ImageVector, text: String) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Brass,
            modifier = Modifier.height(18.dp).width(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = onSurface.copy(alpha = 0.8f)
        )
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
    var showLabels by remember { mutableStateOf(false) }
    var showAddMenu by remember { mutableStateOf(false) }
    var checklistItems by remember { mutableStateOf(listOf<ChecklistItem>()) }
    var newChecklistItem by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(listOf<Uri>()) }
    var labels by remember { mutableStateOf(listOf<String>()) }
    var newLabel by remember { mutableStateOf("") }
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
                    DropdownMenuItem(
                        text = { Text("Labels") },
                        onClick = {
                            showLabels = true
                            showAddMenu = false
                        }
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (showChecklist) {
                        Text("Checklist", color = Moss.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    }
                    if (showAttachments) {
                        Text("Images", color = Moss.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    }
                    if (showLabels || labels.isNotEmpty()) {
                        Text("Labels", color = Moss.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                    }
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
            if (showLabels || labels.isNotEmpty()) {
                Text("Labels", style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        label = { Text("Add label") },
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
                        Text("ADD")
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
                                        contentDescription = "Remove",
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
                onClick = { onCreate(content, checklistItems, labels, pinned, attachments, expiresAt, readOnce) },
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
    onUpdateNoteText: (String, String) -> Unit,
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
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember(note.id) { mutableStateOf(note.text) }
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
                IconButton(onClick = {
                    if (isEditing) {
                        isEditing = false
                        editText = note.text
                    } else {
                        isEditing = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = if (isEditing) "Cancel edit" else "Edit note",
                        tint = if (isEditing) Brass else onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(onClick = { onTogglePinned(note.id) }) {
                    Icon(
                        imageVector = if (note.pinned) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (note.pinned) "Unpin" else "Pin",
                        tint = if (note.pinned) Brass else onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (isEditing) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                        autoCorrectEnabled = false
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else if (note.text.isNotBlank()) {
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
                            .fillMaxWidth()
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
                            contentDescription = "Hold to reorder",
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
                if (isEditing) {
                    Button(
                        onClick = {
                            onUpdateNoteText(note.id, editText)
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text("SAVE")
                    }
                    TextButton(onClick = { isEditing = false; editText = note.text }) {
                        Text("CANCEL", color = onSurface.copy(alpha = 0.7f))
                    }
                } else {
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

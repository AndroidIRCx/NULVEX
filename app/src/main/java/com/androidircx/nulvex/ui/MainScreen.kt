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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.HelpOutline
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import com.androidircx.nulvex.i18n.tx
import com.androidircx.nulvex.R
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
import android.graphics.Bitmap
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    onRequestDecoyBiometricEnroll: (String) -> Unit = {},
    onRequestDecoyBiometricUnlock: () -> Unit = {},
    onDisableDecoyBiometric: () -> Unit = {},
    onChangeRealPin: (String, String, String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateLanguage: (String) -> Unit = {},
    onOpenNew: () -> Unit,
    onCreate: (String, List<ChecklistItem>, List<String>, Boolean, List<android.net.Uri>, Long?, Boolean) -> Unit,
    onOpenNote: (String) -> Unit,
    onCloseNote: () -> Unit,
    onUpdateNoteText: (String, String, Long?) -> Unit,
    onShareNote: (String) -> Unit = {},
    onDelete: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleChecklistItem: (String, String) -> Unit,
    onAddChecklistItem: (String, String) -> Unit,
    onRemoveChecklistItem: (String, String) -> Unit,
    onUpdateChecklistText: (String, String, String) -> Unit,
    onMoveChecklistItem: (String, String, Int) -> Unit,
    onAddLabel: (String, String) -> Unit,
    onRemoveLabel: (String, String) -> Unit,
    onCreateStandaloneLabel: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onSelectLabel: (String?) -> Unit,
    onLoadAttachmentPreview: (String, String) -> Unit,
    onRemoveAttachment: (String, String) -> Unit,
    onClearError: () -> Unit,
    onWatchAdToRemoveAds: () -> Unit = {},
    onWatchAdForShares: () -> Unit = {},
    onOpenPurchases: () -> Unit = {},
    onClosePurchases: () -> Unit = {},
    onBuyRemoveAds: () -> Unit = {},
    onBuyProFeatures: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onImportSharedKey: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteSharedKey: (String) -> Unit = {},
    onUploadBackup: (String) -> Unit = {},
    onRestoreBackup: (String, String, Boolean, String?, Long?) -> Unit = { _, _, _, _, _ -> },
    onRestoreSavedBackup: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteSavedBackup: (String) -> Unit = {},
    onScanQrKey: () -> Unit = {},
    onExportLocalBackup: (String) -> Unit = {},
    onImportLocalBackup: (String, Boolean) -> Unit = { _, _ -> },
    onExportKeyManager: (Boolean, String?) -> Unit = { _, _ -> },
    onImportKeyManager: (String?) -> Unit = {},
    onGenerateXChaChaKey: (String) -> Unit = {},
    onGeneratePgpKey: (String) -> Unit = {},
    onBuildKeyTransferPayload: (String) -> String? = { null },
    onStartNfcKeyShare: (String) -> Unit = {},
    onNoteEditDraftChanged: (String, String, Long?) -> Unit = { _, _, _ -> },
    onClearNoteEditDraft: () -> Unit = {},
    onNewNoteDraftChanged: (NewNoteDraft?) -> Unit = {},
    onImportIncomingFile: (ByteArray, String, String, Boolean) -> Unit = { _, _, _, _ -> },
    onImportIncomingKeyManager: (ByteArray, String?) -> Unit = { _, _ -> },
    onImportIncomingRemote: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onClearPendingImport: () -> Unit = {}
) {
    var showPanicConfirm by remember { mutableStateOf(false) }
    var showLabelMenu by remember { mutableStateOf(false) }
    var decoyTapCount by remember { mutableStateOf(0) }
    var decoyVisible by remember { mutableStateOf(false) }
    // Auto-reset tap counter after 2 seconds of inactivity
    LaunchedEffect(decoyTapCount) {
        if (decoyTapCount > 0 && decoyTapCount < 6) {
            delay(2000)
            decoyTapCount = 0
        }
    }
    AppBackground {
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = showLabelMenu) {
                LabelsMenu(
                    state = state,
                    onSelectLabel = onSelectLabel,
                    onAddLabel = onAddLabel,
                    onRemoveLabel = onRemoveLabel,
                    onCreateStandaloneLabel = onCreateStandaloneLabel,
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
                    onCloseSettings = if (state.screen == Screen.Purchases) onClosePurchases else onCloseSettings,
                    onToggleLabels = { showLabelMenu = !showLabelMenu },
                    onLogoTap = {
                        decoyTapCount++
                        if (decoyTapCount >= 6) {
                            decoyVisible = true
                            decoyTapCount = 0
                        }
                    },
                    decoyTapCount = decoyTapCount,
                    decoyUnlocked = decoyVisible
                )
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(350)) + slideInVertically(animationSpec = tween(350)) { 40 }
                ) {
                    when (state.screen) {
                        Screen.Onboarding -> OnboardingScreen(
                            languageTag = state.languageTag,
                            onSelectLanguage = onUpdateLanguage,
                            onComplete = onCompleteOnboarding
                        )
                        Screen.Setup -> SetupScreen(state, onSetup)
                        Screen.Unlock -> UnlockScreen(state, onUnlock, onRequestBiometricUnlock, onRequestDecoyBiometricUnlock)
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
                            decoyVisible = decoyVisible,
                            onUpdateDecoyPin = onUpdateDecoyPin,
                            onDisableDecoy = onDisableDecoy,
                            onUpdateLockTimeout = onUpdateLockTimeout,
                            onUpdateDefaultExpiry = onUpdateDefaultExpiry,
                            onUpdateDefaultReadOnce = onUpdateDefaultReadOnce,
                            onDisableBiometric = onDisableBiometric,
                            onRequestBiometricEnroll = onRequestBiometricEnroll,
                            onRequestDecoyBiometricEnroll = onRequestDecoyBiometricEnroll,
                            onDisableDecoyBiometric = onDisableDecoyBiometric,
                            onChangeRealPin = onChangeRealPin,
                            onUpdateThemeMode = onUpdateThemeMode,
                            onUpdateLanguage = onUpdateLanguage,
                            onClose = {
                                decoyVisible = false
                                decoyTapCount = 0
                                onCloseSettings()
                            },
                            onWatchAdToRemoveAds = onWatchAdToRemoveAds,
                            onWatchAdForShares = onWatchAdForShares,
                            onOpenPurchases = onOpenPurchases,
                            onImportSharedKey = onImportSharedKey,
                            onDeleteSharedKey = onDeleteSharedKey,
                            onUploadBackup = onUploadBackup,
                            onRestoreBackup = onRestoreBackup,
                            onRestoreSavedBackup = onRestoreSavedBackup,
                            onDeleteSavedBackup = onDeleteSavedBackup,
                            onScanQrKey = onScanQrKey,
                            onExportLocalBackup = onExportLocalBackup,
                            onImportLocalBackup = onImportLocalBackup,
                            onExportKeyManager = onExportKeyManager,
                            onImportKeyManager = onImportKeyManager,
                            onGenerateXChaChaKey = onGenerateXChaChaKey,
                            onGeneratePgpKey = onGeneratePgpKey,
                            onBuildKeyTransferPayload = onBuildKeyTransferPayload,
                            onStartNfcKeyShare = onStartNfcKeyShare
                        )
                        Screen.Purchases -> PurchaseScreen(
                            state = state,
                            onBack = onClosePurchases,
                            onBuyRemoveAds = onBuyRemoveAds,
                            onBuyProFeatures = onBuyProFeatures,
                            onRestorePurchases = onRestorePurchases
                        )
                        Screen.NewNote -> NewNoteScreen(
                            state = state,
                            onCreate = onCreate,
                            onCancel = onCloseNote,
                            defaultExpiry = state.defaultExpiry,
                            defaultReadOnce = state.defaultReadOnce,
                            onDraftChanged = onNewNoteDraftChanged
                        )
                        Screen.NoteDetail -> NoteDetailScreen(
                            state,
                            onCloseNote,
                            onUpdateNoteText,
                            onShareNote,
                            onDelete,
                            onTogglePinned,
                            onToggleChecklistItem,
                            onAddChecklistItem,
                            onRemoveChecklistItem,
                            onUpdateChecklistText,
                            onMoveChecklistItem,
                            onLoadAttachmentPreview,
                            onRemoveAttachment,
                            onNoteEditDraftChanged,
                            onClearNoteEditDraft
                        )
                    }
                }
                } // end inner padding Column
            }
        }
        ErrorBar(state, onClearError)
        if (state.pendingImport != null && state.screen == Screen.Vault) {
            PendingImportDialog(
                state = state,
                onImportFile = onImportIncomingFile,
                onImportKeyManager = onImportIncomingKeyManager,
                onImportRemote = onImportIncomingRemote,
                onDismiss = onClearPendingImport
            )
        }
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
                        Text(tx("PANIC WIPE"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Ember
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(tx("All vault data will be permanently destroyed. This cannot be undone."),
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
                                text = if (holdProgress > 0.01f) tx("WIPING...") else tx("HOLD TO WIPE"),
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
                            Text(tx("CANCEL"), color = Sand.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

internal fun resolveRemoteMediaIdInput(rawInput: String): String {
    val trimmed = rawInput.trim()
    if (trimmed.isBlank()) return ""
    val marker = "/api/media/download/"
    return if (trimmed.contains(marker)) {
        trimmed.substringAfter(marker).substringBefore("?").substringBefore("#").trim().trim('/')
    } else {
        trimmed
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
 * Full-width banner ad row with a localized remove-ads text button below it.
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
                    text = tx("Remove ads (10 min)"),
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
    onToggleLabels: () -> Unit,
    onLogoTap: () -> Unit = {},
    decoyTapCount: Int = 0,
    decoyUnlocked: Boolean = false
) {
    val onBackground = MaterialTheme.colorScheme.onBackground

    // 6 bolt targets (dx, dy in dp) — one per tap, spread in different directions
    val boltTargets = remember {
        listOf(
            Pair(80f, -55f),    // tap 1: up-right
            Pair(0f,  -85f),    // tap 2: straight up
            Pair(-80f, -55f),   // tap 3: up-left
            Pair(100f,  5f),    // tap 4: right
            Pair(-100f,  5f),   // tap 5: left
            Pair(55f,  -72f),   // tap 6: up-right (steeper, not used in success)
        )
    }

    val boltAlphas = remember { List(6) { Animatable(0f) } }
    val boltX     = remember { List(6) { Animatable(0f) } }
    val boltY     = remember { List(6) { Animatable(0f) } }

    var textFlash by remember { mutableStateOf(false) }
    val flashColor by animateColorAsState(
        targetValue = if (textFlash) Color(0xFFFFFF55) else onBackground,
        animationSpec = tween(55),
        label = "lightning_flash"
    )
    val shakeOffset by animateFloatAsState(
        targetValue = if (textFlash) 6f else 0f,
        animationSpec = spring(dampingRatio = 0.15f, stiffness = 1800f),
        label = "lightning_shake"
    )

    var prevCount by remember { mutableStateOf(0) }
    LaunchedEffect(decoyTapCount) {
        val prev = prevCount
        prevCount = decoyTapCount
        when {
            // 6th tap success — all active bolts explode outward and vanish
            decoyTapCount == 0 && prev >= 5 && decoyUnlocked -> {
                textFlash = true
                (0 until 6).forEach { i ->
                    launch {
                        val (tx, ty) = boltTargets[i]
                        launch { boltX[i].animateTo(tx * 2.2f, tween(300, easing = FastOutSlowInEasing)) }
                        launch { boltY[i].animateTo(ty * 2.2f, tween(300, easing = FastOutSlowInEasing)) }
                        boltAlphas[i].animateTo(0f, tween(300))
                    }
                }
                delay(150)
                textFlash = false
            }

            // Timeout reset — active bolts fade out smoothly
            decoyTapCount == 0 && prev > 0 -> {
                textFlash = false
                (0 until prev.coerceAtMost(6)).forEach { i ->
                    launch { boltAlphas[i].animateTo(0f, tween(400)) }
                }
            }

            // Taps 1–5 — fire the corresponding bolt + flash text
            decoyTapCount in 1..5 -> {
                val idx = decoyTapCount - 1
                val (tx, ty) = boltTargets[idx]
                // Reset this bolt to the logo origin
                boltX[idx].snapTo(0f)
                boltY[idx].snapTo(0f)
                boltAlphas[idx].snapTo(1f)
                // Flash text color and shake
                textFlash = true
                // Fly outward
                launch { boltX[idx].animateTo(tx, tween(620, easing = FastOutSlowInEasing)) }
                launch { boltY[idx].animateTo(ty, tween(620, easing = FastOutSlowInEasing)) }
                delay(85)
                textFlash = false
                // Fade bolt after it reaches destination
                delay(420)
                boltAlphas[idx].animateTo(0f, tween(230))
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            // ⚡ Lightning bolts flying out from the logo
            boltTargets.forEachIndexed { i, _ ->
                Text(
                    text = "⚡",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .offset(x = boltX[i].value.dp, y = boltY[i].value.dp)
                        .alpha(boltAlphas[i].value)
                )
            }
            // NULVEX logo — shakes and flashes on each tap
            Column(
                modifier = Modifier
                    .offset(x = shakeOffset.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onLogoTap
                    )
            ) {
                Text(
                    text = tx("NULVEX"),
                    style = MaterialTheme.typography.displayLarge,
                    color = flashColor
                )
                Text(
                    text = tx("Offline secure vault"),
                    style = MaterialTheme.typography.labelLarge,
                    color = flashColor.copy(alpha = 0.7f)
                )
            }
        }

        if (state.screen == Screen.Vault || state.screen == Screen.NoteDetail || state.screen == Screen.NewNote) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onToggleLabels) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = tx("Labels"),
                        tint = onBackground.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = tx("Settings"),
                        tint = onBackground.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onPanicClick) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = tx("Panic wipe"),
                        tint = Ember
                    )
                }
                IconButton(onClick = onLock) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = tx("Lock"),
                        tint = Brass
                    )
                }
            }
        } else if (state.screen == Screen.Settings || state.screen == Screen.Purchases) {
            IconButton(onClick = onCloseSettings) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = tx("Back"),
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
    val scope = androidx.compose.runtime.rememberCoroutineScope()
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
            Text(tx("Create your vault"), style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text(tx("Primary PIN")) },
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
                label = { Text(tx("Confirm PIN")) },
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
                Text(tx("Enable decoy vault"), color = onSurface)
            }
            if (decoyEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = decoyPin,
                    onValueChange = { decoyPin = it },
                    label = { Text(tx("Decoy PIN")) },
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
                Text(tx("Enable fingerprint unlock"), color = onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))
            val pinMismatch = pin.isNotEmpty() && confirm.isNotEmpty() && pin != confirm
            if (pinMismatch) {
                Text(tx("PINs do not match"), color = Ember, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    onSetup(pin, if (decoyEnabled) decoyPin else null, enableBiometric)
                },
                enabled = !state.isBusy && pin.isNotBlank() && !pinMismatch,
                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
            ) {
                Text(tx("CREATE VAULT"))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UnlockScreen(
    state: UiState,
    onUnlock: (String) -> Unit,
    onRequestBiometricUnlock: () -> Unit,
    onRequestDecoyBiometricUnlock: () -> Unit = {}
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
            Text(tx("Unlock"), style = MaterialTheme.typography.titleLarge, color = Sand)
            Spacer(modifier = Modifier.height(28.dp))
            SecurePinPad(
                pin = pin,
                label = tx("Enter PIN"),
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
                    val lockoutTemplate = tx("Too many attempts. Try again in {seconds}s")
                    Text(
                        text = lockoutTemplate.replace("{seconds}", lockoutRemainingSecs.toString()),
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
                    Text(tx("UNLOCK"))
                }
            }
            if (state.biometricEnabled && !isLockedOut) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            enabled = !state.isBusy,
                            onClick = onRequestBiometricUnlock,
                            onLongClick = if (state.decoyBiometricEnabled) onRequestDecoyBiometricUnlock else null
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tx("UNLOCK WITH FINGERPRINT"),
                        color = Sand.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
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
    val sortLabels = mapOf(
        SortMode.NEWEST to tx("Newest"),
        SortMode.OLDEST to tx("Oldest"),
        SortMode.EXPIRING to tx("Expiring soon")
    )

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
                    val notesBadge = tx("{count} notes").replace("{count}", filteredNotes.size.toString())
                    VaultBadge(text = notesBadge, tint = Sand)
                    if (readOnceCount > 0) {
                        VaultBadge(text = "$readOnceCount ${tx("burn")}", tint = Brass)
                    }
                    if (expiringCount > 0) {
                        VaultBadge(text = "$expiringCount ${tx("expiring")}", tint = Ember)
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
                            sortLabels[sortMode] ?: tx(sortMode.label),
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
                                        sortLabels[mode] ?: tx(mode.label),
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
                        SectionLabel(if (pinnedNotes.isEmpty()) tx("Notes") else tx("Others"))
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
            title = { Text(tx("Delete note?"), color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(tx("This action cannot be undone."),
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
    decoyVisible: Boolean = false,
    onUpdateDecoyPin: (String, String) -> Unit,
    onDisableDecoy: () -> Unit,
    onUpdateLockTimeout: (Long) -> Unit,
    onUpdateDefaultExpiry: (String) -> Unit,
    onUpdateDefaultReadOnce: (Boolean) -> Unit,
    onDisableBiometric: () -> Unit,
    onRequestBiometricEnroll: (String) -> Unit,
    onRequestDecoyBiometricEnroll: (String) -> Unit = {},
    onDisableDecoyBiometric: () -> Unit = {},
    onChangeRealPin: (String, String, String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateLanguage: (String) -> Unit,
    onClose: () -> Unit,
    onWatchAdToRemoveAds: () -> Unit = {},
    onWatchAdForShares: () -> Unit = {},
    onOpenPurchases: () -> Unit = {},
    onImportSharedKey: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteSharedKey: (String) -> Unit = {},
    onUploadBackup: (String) -> Unit = {},
    onRestoreBackup: (String, String, Boolean, String?, Long?) -> Unit = { _, _, _, _, _ -> },
    onRestoreSavedBackup: (String, Boolean) -> Unit = { _, _ -> },
    onDeleteSavedBackup: (String) -> Unit = {},
    onScanQrKey: () -> Unit = {},
    onExportLocalBackup: (String) -> Unit = {},
    onImportLocalBackup: (String, Boolean) -> Unit = { _, _ -> },
    onExportKeyManager: (Boolean, String?) -> Unit = { _, _ -> },
    onImportKeyManager: (String?) -> Unit = {},
    onGenerateXChaChaKey: (String) -> Unit = {},
    onGeneratePgpKey: (String) -> Unit = {},
    onBuildKeyTransferPayload: (String) -> String? = { null },
    onStartNfcKeyShare: (String) -> Unit = {}
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val clipboard = LocalClipboardManager.current
    var decoyPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var decoyBiometricPin by remember { mutableStateOf("") }
    val pinMismatch = decoyPin.isNotEmpty() && confirmPin.isNotEmpty() && decoyPin != confirmPin
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }
    var biometricPin by remember { mutableStateOf("") }
    val realPinMismatch = newPin.isNotEmpty() && confirmNewPin.isNotEmpty() && newPin != confirmNewPin
    var settingsSearch by remember { mutableStateOf("") }
    var expandedSections by remember { mutableStateOf(setOf<String>()) }
    var keyLabel by remember { mutableStateOf("") }
    var keyInput by remember { mutableStateOf("") }
    var selectedKeyId by remember { mutableStateOf("") }
    var restoreMediaId by remember { mutableStateOf(state.lastBackupMediaId) }
    var selectedBackupRecordId by remember { mutableStateOf("") }
    var restoreMerge by remember { mutableStateOf(true) }
    var keyManagerExportEncrypted by remember { mutableStateOf(true) }
    var keyManagerPassword by remember { mutableStateOf("") }
    var keyManagerImportPassword by remember { mutableStateOf("") }
    var keyShareDialog by remember { mutableStateOf(false) }
    var keyQrPayload by remember { mutableStateOf<String?>(null) }
    var keyQrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var infoDialogText by remember { mutableStateOf<String?>(null) }
    var generationSuccessDialog by remember { mutableStateOf<String?>(null) }
    var confirmDeleteKeyId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteBackupId by remember { mutableStateOf<String?>(null) }
    var lastHandledStatus by remember { mutableStateOf("") }
    val settingsScroll = rememberScrollState()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
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
        "ad-free time",
        "purchase",
        "pro"
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
    val showDanger = decoyVisible && matchesSection(
        "danger zone",
        "decoy",
        "decoy pin",
        "wipe",
        "coercion",
        "plausible deniability"
    )
    val showKeys = matchesSection(
        "keys",
        "pgp",
        "xchacha",
        "nfc",
        "qr",
        "key manager"
    )
    val showBackup = matchesSection(
        "backup",
        "restore",
        "encrypted backup",
        "local backup",
        "remote backup"
    )
    val showAbout = matchesSection("about", "version", "nulvex", "offline", "xchacha20", "kyber768")
    val hasVisibleSections = showAds || showDisplay || showVaultDefaults || showSecurity || showDanger || showKeys || showBackup || showAbout

    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            nowMs = System.currentTimeMillis()
        }
    }

    val remainingMs = maxOf(0L, state.adFreeUntil - nowMs)
    val adFreeActive = remainingMs > 0L

    LaunchedEffect(state.backupStatus) {
        val status = state.backupStatus
        if (status.isBlank() || status == lastHandledStatus) return@LaunchedEffect
        if (status == "XChaCha key generated" || status == "OpenPGP key generated") {
            generationSuccessDialog = "Key created successfully."
            lastHandledStatus = status
        }
    }

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
                .verticalScroll(settingsScroll)
                .imePadding()
        ) {
            Text(tx("Settings"), style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = settingsSearch,
                onValueChange = { settingsSearch = it },
                label = { Text(tx("Search settings")) },
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
                                contentDescription = tx("Clear search")
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
                    title = tx("Rewards & Ads"),
                    description = tx("Remove ads and earn share credits"),
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
                            Text(tx("Ad-free time"), color = onSurface)
                            Text(
                                if (adFreeActive) "${formatRemaining(remainingMs)} ${tx("remaining")}" else tx("Ads are active"),
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
                                Text(tx("ACTIVE"), style = MaterialTheme.typography.labelSmall, color = Moss)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onWatchAdToRemoveAds,
                        enabled = !state.isAdFree,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (state.isAdFree) tx("ADS REMOVED")
                            else if (adFreeActive) tx("EXTEND BY 10 MIN")
                            else tx("WATCH AD - 10 MIN NO ADS")
                        )
                    }
                    Text(
                        if (state.isAdFree) tx("Lifetime remove-ads purchase is active.")
                        else tx("Stacks - watch multiple times to bank more ad-free minutes."),
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
                            Text(tx("Share credits"), color = onSurface)
                            Text(tx("Used to share notes via the secure API"),
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
                                if (state.hasProFeatures) tx("UNLIMITED") else "${state.shareCredits}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Brass
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (state.hasProFeatures) {
                        Text(tx("Pro Features lifetime purchase is active. You have unlimited shares."),
                            style = MaterialTheme.typography.bodySmall,
                            color = Moss
                        )
                    } else {
                        Button(
                            onClick = onWatchAdForShares,
                            colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(tx("WATCH AD - EARN 1 SHARE CREDIT"))
                        }
                        Text(tx("Credits accumulate - watch 3 ads to earn 3 shares."),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(tx("One-time purchases: remove ads lifetime and Pro features lifetime."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onOpenPurchases,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(tx("OPEN PURCHASE OPTIONS"))
                    }
                }
            }

            if (showDisplay) {
                if (showAds) SettingsDivider()
                SettingsSection(
                    icon = Icons.Filled.Palette,
                    title = tx("Display"),
                    description = tx("Appearance and theme"),
                    expanded = isExpanded("display"),
                    onToggle = { toggleSection("display") }
                ) {
                    Text(tx("Theme"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip(tx("System"), state.themeMode == ThemeMode.SYSTEM) { onUpdateThemeMode(ThemeMode.SYSTEM) }
                        Chip(tx("Dark"), state.themeMode == ThemeMode.DARK) { onUpdateThemeMode(ThemeMode.DARK) }
                        Chip(tx("Light"), state.themeMode == ThemeMode.LIGHT) { onUpdateThemeMode(ThemeMode.LIGHT) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.settings_language_title), style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Chip(stringResource(R.string.settings_language_system), state.languageTag == "system") { onUpdateLanguage("system") }
                        Chip(tx("English"), state.languageTag == "en") { onUpdateLanguage("en") }
                        Chip(tx("Serbian"), state.languageTag == "sr") { onUpdateLanguage("sr") }
                    }
                }
            }

            if (showVaultDefaults && (showAds || showDisplay)) SettingsDivider()

            // === VAULT DEFAULTS SECTION ===
            if (showVaultDefaults) SettingsSection(
                icon = Icons.Filled.Timer,
                title = tx("Vault defaults"),
                description = tx("Auto-lock and note behavior"),
                expanded = isExpanded("vault_defaults"),
                onToggle = { toggleSection("vault_defaults") }
            ) {
                Text(tx("Auto-lock timeout"), style = MaterialTheme.typography.labelLarge, color = onSurface)
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
                        Chip(tx(label), state.lockTimeoutMs == value) { onUpdateLockTimeout(value) }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(tx("Default self-destruct"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Text(tx("New notes will use this expiry setting"),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val expiryScroll = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(expiryScroll)
                ) {
                    Chip(tx("None"), state.defaultExpiry == "none") { onUpdateDefaultExpiry("none") }
                    Chip(tx("1h"), state.defaultExpiry == "1h") { onUpdateDefaultExpiry("1h") }
                    Chip(tx("24h"), state.defaultExpiry == "24h") { onUpdateDefaultExpiry("24h") }
                    Chip(tx("7d"), state.defaultExpiry == "7d") { onUpdateDefaultExpiry("7d") }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.defaultReadOnce,
                        onCheckedChange = onUpdateDefaultReadOnce
                    )
                    Column {
                        Text(tx("Read-once by default"), color = onSurface)
                        Text(tx("Notes are deleted after first read"),
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
                title = tx("Security"),
                description = tx("Authentication and encryption"),
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
                        Text(tx("Fingerprint unlock"), color = onSurface)
                        Text(
                            if (state.biometricEnabled) tx("Enabled") else tx("Disabled"),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.biometricEnabled) Moss else onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (state.biometricEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDisableBiometric, enabled = !state.isBusy) {
                        Text(tx("DISABLE FINGERPRINT"), color = Ember)
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = biometricPin,
                        onValueChange = { biometricPin = it },
                        label = { Text(tx("Current PIN to enable")) },
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
                        Text(tx("ENABLE FINGERPRINT"))
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
                        Text(tx("Change primary PIN"), color = onSurface)
                        Text(tx("Re-encrypts your entire vault"),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = { currentPin = it },
                    label = { Text(tx("Current PIN")) },
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
                    label = { Text(tx("New PIN")) },
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
                    label = { Text(tx("Confirm new PIN")) },
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
                    Text(tx("PINs do not match"), color = Ember, style = MaterialTheme.typography.labelMedium)
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
                    Text(tx("CHANGE PIN"))
                }
                if (state.isBusy) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(tx("Rekeying vault..."), color = Brass, style = MaterialTheme.typography.labelMedium)
                }
            }

            if (showDanger && (showAds || showDisplay || showVaultDefaults || showSecurity)) SettingsDivider()

            // === DANGER ZONE SECTION ===
            if (showDanger) SettingsSection(
                icon = Icons.Filled.VisibilityOff,
                title = tx("Danger zone"),
                description = tx("Decoy vault and destructive actions"),
                accentColor = Ember,
                expanded = isExpanded("danger"),
                onToggle = { toggleSection("danger") }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx("Decoy vault"), color = onSurface)
                        Text(
                            if (state.isDecoyEnabled) tx("Active - separate fake vault") else tx("Disabled"),
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
                            Text(tx("ENABLED"), style = MaterialTheme.typography.labelSmall, color = Brass)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    tx("A decoy vault opens when you enter a different PIN.") + " " +
                        tx("Use it for plausible deniability under coercion."),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = decoyPin,
                    onValueChange = { decoyPin = it },
                    label = { Text(if (state.isDecoyEnabled) tx("New decoy PIN") else tx("Set decoy PIN")) },
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
                    label = { Text(tx("Confirm decoy PIN")) },
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
                    Text(tx("PINs do not match"), color = Ember, style = MaterialTheme.typography.labelMedium)
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
                        Text(if (state.isDecoyEnabled) tx("CHANGE") else tx("ENABLE"))
                    }
                    if (state.isDecoyEnabled) {
                        TextButton(onClick = onDisableDecoy, enabled = !state.isBusy) {
                            Text(tx("DISABLE"), color = Ember)
                        }
                    }
                }

                if (state.isDecoyEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tx("Changing the decoy PIN wipes the old decoy vault."),
                        style = MaterialTheme.typography.bodySmall,
                        color = Ember.copy(alpha = 0.8f)
                    )
                }

                if (state.isDecoyEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx("Decoy fingerprint"), color = onSurface)
                            Text(
                                if (state.decoyBiometricEnabled) tx("Enabled - long-press fingerprint to unlock decoy") else tx("Disabled"),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.decoyBiometricEnabled) Brass else onSurface.copy(alpha = 0.6f)
                            )
                        }
                        if (state.decoyBiometricEnabled) {
                            Box(
                                modifier = Modifier
                                    .background(Brass.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(tx("ON"), style = MaterialTheme.typography.labelSmall, color = Brass)
                            }
                        }
                    }
                    if (!state.decoyBiometricEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = decoyBiometricPin,
                            onValueChange = { decoyBiometricPin = it },
                            label = { Text(tx("Decoy PIN (to enroll fingerprint)")) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done,
                                autoCorrectEnabled = false
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (!state.decoyBiometricEnabled) {
                            Button(
                                onClick = {
                                    onRequestDecoyBiometricEnroll(decoyBiometricPin)
                                    decoyBiometricPin = ""
                                },
                                enabled = !state.isBusy && decoyBiometricPin.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                            ) {
                                Text(tx("ENABLE FINGERPRINT"))
                            }
                        } else {
                            TextButton(onClick = onDisableDecoyBiometric, enabled = !state.isBusy) {
                                Text(tx("DISABLE FINGERPRINT"), color = Ember)
                            }
                        }
                    }
                }
            }

            if (showKeys && (showAds || showDisplay || showVaultDefaults || showSecurity || showDanger)) SettingsDivider()

            if (showKeys) SettingsSection(
                icon = Icons.Filled.Shield,
                title = tx("Keys Manager"),
                description = tx("OpenPGP + XChaCha key storage"),
                expanded = isExpanded("keys"),
                onToggle = { toggleSection("keys") }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(tx("What is this?"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        infoDialogText = "info_keys_manager_overview"
                    }) {
                        Icon(Icons.Filled.HelpOutline, contentDescription = tx("What is Keys Manager?"), tint = Brass)
                    }
                }
                OutlinedTextField(
                    value = keyLabel,
                    onValueChange = { keyLabel = it },
                    label = { Text(tx("Key label")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    label = { Text(tx("Manual import (OpenPGP armored or XChaCha key)")) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(tx("Manual key format help"),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        infoDialogText = "info_manual_import"
                    }) {
                        Icon(Icons.Filled.HelpOutline, contentDescription = tx("Manual format help"), tint = Moss)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onImportSharedKey(keyLabel, "manual", keyInput)
                        keyInput = ""
                    },
                    enabled = !state.isBusy && keyInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(tx("IMPORT KEY"))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onGenerateXChaChaKey(keyLabel.ifBlank { "XChaCha key" }) },
                        enabled = !state.isBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) { Text(tx("GENERATE XCHACHA")) }
                    Button(
                        onClick = { onGeneratePgpKey(keyLabel.ifBlank { "OpenPGP key" }) },
                        enabled = !state.isBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) { Text(tx("GENERATE PGP")) }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(tx("Generate help"), style = MaterialTheme.typography.bodySmall, color = onSurface.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        infoDialogText = "info_generate_help"
                    }) {
                        Icon(Icons.Filled.HelpOutline, contentDescription = tx("Generate help"), tint = Moss)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onScanQrKey,
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(tx("SCAN QR KEY"))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(tx("NFC import: tap an NFC tag while the app is open."),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(tx("QR/NFC exchange help"), style = MaterialTheme.typography.bodySmall, color = onSurface.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = {
                        infoDialogText = "info_qr_nfc_exchange"
                    }) {
                        Icon(Icons.Filled.HelpOutline, contentDescription = tx("QR/NFC help"), tint = Moss)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = keyManagerExportEncrypted,
                        onCheckedChange = { keyManagerExportEncrypted = it }
                    )
                    Text(tx("Encrypt key manager export with password"), color = onSurface)
                }
                if (keyManagerExportEncrypted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = keyManagerPassword,
                        onValueChange = { keyManagerPassword = it },
                        label = { Text(tx("Export password")) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        onExportKeyManager(keyManagerExportEncrypted, keyManagerPassword.ifBlank { null })
                    },
                    enabled = !state.isBusy && (!keyManagerExportEncrypted || keyManagerPassword.isNotBlank()),
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(tx("EXPORT KEY MANAGER")) }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = keyManagerImportPassword,
                    onValueChange = { keyManagerImportPassword = it },
                    label = { Text(tx("Import password (if file encrypted)")) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onImportKeyManager(keyManagerImportPassword.ifBlank { null }) },
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(tx("IMPORT KEY MANAGER")) }

                if (state.sharedKeys.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(tx("Imported keys"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    state.sharedKeys.forEach { key ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(key.label, color = onSurface)
                                val sourceLabel = when (key.source.lowercase()) {
                                    "generated" -> tx("generated")
                                    "manual" -> tx("manual")
                                    "qr" -> tx("qr")
                                    "nfc" -> tx("nfc")
                                    else -> key.source
                                }
                                Text("${tx("via")} $sourceLabel - ${key.format} - ${key.fingerprint}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onSurface.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            TextButton(onClick = { selectedKeyId = key.id }) {
                                Text(if (selectedKeyId == key.id) tx("SELECTED") else tx("SELECT"), color = Brass)
                            }
                            TextButton(onClick = { confirmDeleteKeyId = key.id }) { Text(tx("DELETE"), color = Ember) }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    if (selectedKeyId.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { keyShareDialog = true },
                            enabled = !state.isBusy,
                            colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(tx("SHARE SELECTED KEY")) }
                    }
                }
                if (state.backupStatus.isNotBlank()) {
                    val localizedStatus = when (state.backupStatus) {
                        "Encrypted note file ready for share" -> tx("Encrypted note file ready for share")
                        else -> tx(state.backupStatus)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        localizedStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = Moss
                    )
                }
            }

            if (showBackup && (showAds || showDisplay || showVaultDefaults || showSecurity || showDanger || showKeys)) SettingsDivider()

            if (showBackup) SettingsSection(
                icon = Icons.Filled.Timer,
                title = tx("Backup"),
                description = tx("Local encrypted backup + optional Pro remote"),
                expanded = isExpanded("backup"),
                onToggle = { toggleSection("backup") }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        tx("Local backup exports") +
                            " ${com.androidircx.nulvex.pro.NulvexFileTypes.BACKUP_EXT}. " +
                            tx("Remote backup uploads encrypted blobs (Pro)."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        infoDialogText = "info_backup_modes"
                    }) {
                        Icon(Icons.Filled.HelpOutline, contentDescription = tx("Backup help"), tint = Brass)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedKeyId.isNotBlank()) {
                    Button(
                        onClick = { onExportLocalBackup(selectedKeyId) },
                        enabled = !state.isBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(tx("EXPORT ENCRYPTED BACKUP FILE")) }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onImportLocalBackup(selectedKeyId, restoreMerge) },
                        enabled = !state.isBusy,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(tx("IMPORT LOCAL BACKUP FILE")) }
                } else {
                    Text(tx("Select a key in Keys Manager first."), color = Ember, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = restoreMerge, onCheckedChange = { restoreMerge = it })
                    Column {
                        Text(tx("Merge with existing notes"), color = onSurface)
                        Text(tx("Disable to replace current vault notes"), style = MaterialTheme.typography.bodySmall, color = onSurface.copy(alpha = 0.6f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    if (state.hasProFeatures) tx("Pro remote backup is active") else tx("Remote media backup requires Pro"),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.hasProFeatures) Moss else Ember
                )
                if (selectedKeyId.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onUploadBackup(selectedKeyId) },
                        enabled = !state.isBusy && state.hasProFeatures,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(tx("UPLOAD TO REMOTE MEDIA SERVER")) }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = restoreMediaId,
                    onValueChange = { restoreMediaId = it },
                    label = { Text(tx("Download link or media ID")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val resolvedMediaId = resolveRemoteMediaIdInput(restoreMediaId)
                        onRestoreBackup(resolvedMediaId, selectedKeyId, restoreMerge, null, null)
                    },
                    enabled = !state.isBusy &&
                        state.hasProFeatures &&
                        resolveRemoteMediaIdInput(restoreMediaId).isNotBlank() &&
                        selectedKeyId.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(tx("RESTORE FROM REMOTE")) }

                if (state.backupRecords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(tx("Saved remote backups"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    state.backupRecords.forEach { backup ->
                        val selected = selectedBackupRecordId == backup.id
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(backup.mediaId, color = onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    "key=${backup.keyId.take(8)}... token=${if (backup.downloadToken.isNullOrBlank()) "none" else "saved"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    "https://androidircx.com/api/media/download/${backup.downloadPathId}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Moss.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            TextButton(onClick = { selectedBackupRecordId = backup.id }) {
                                Text(if (selected) tx("SELECTED") else tx("SELECT"), color = Brass)
                            }
                            TextButton(onClick = {
                                clipboard.setText(AnnotatedString("https://androidircx.com/api/media/download/${backup.downloadPathId}"))
                            }) { Text(tx("COPY"), color = Moss) }
                            TextButton(onClick = { confirmDeleteBackupId = backup.id }) { Text(tx("DELETE"), color = Ember) }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Button(
                        onClick = { onRestoreSavedBackup(selectedBackupRecordId, restoreMerge) },
                        enabled = !state.isBusy && state.hasProFeatures && selectedBackupRecordId.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(tx("RESTORE SELECTED SAVED BACKUP")) }
                }
                if (state.backupStatus.isNotBlank()) {
                    val localizedStatus = when (state.backupStatus) {
                        "Encrypted note file ready for share" -> tx("Encrypted note file ready for share")
                        else -> tx(state.backupStatus)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(localizedStatus, style = MaterialTheme.typography.bodySmall, color = Moss)
                }
            }

            if (showAbout && (showAds || showDisplay || showVaultDefaults || showSecurity || showDanger || showKeys || showBackup)) {
                SettingsDivider()
            }

            // === ABOUT SECTION ===
            if (showAbout) SettingsSection(
                icon = Icons.Filled.Info,
                title = tx("About"),
                description = tx("App information"),
                expanded = isExpanded("about"),
                onToggle = { toggleSection("about") }
            ) {
                Text(tx("Nulvex"), style = MaterialTheme.typography.titleMedium, color = onSurface)
                Text(tx("Offline-first secure vault"),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                    Text(tx("Version ${BuildConfig.VERSION_NAME}"),
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurface.copy(alpha = 0.5f)
                    )
                Spacer(modifier = Modifier.height(4.dp))
                Text(tx("XChaCha20-Poly1305 + ML-KEM-768"),
                    style = MaterialTheme.typography.labelSmall,
                    color = Moss.copy(alpha = 0.8f)
                )
            }

            if (!hasVisibleSections) {
                Text(tx("No settings match your search."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text(tx("BACK TO VAULT"), color = Brass)
            }
        }
    }
    if (keyShareDialog) {
        AlertDialog(
            onDismissRequest = { keyShareDialog = false },
            title = { Text(tx("Share key")) },
            text = { Text(tx("Choose transfer method: NFC or QR code.")) },
            confirmButton = {
                TextButton(onClick = {
                    val payload = onBuildKeyTransferPayload(selectedKeyId)
                    if (payload != null) {
                        keyQrPayload = payload
                        keyQrBitmap = generateQrBitmap(payload, size = 900)
                        keyShareDialog = false
                    }
                }) { Text(tx("QR CODE")) }
            },
            dismissButton = {
                TextButton(onClick = {
                    val payload = onBuildKeyTransferPayload(selectedKeyId)
                    if (payload != null) {
                        onStartNfcKeyShare(payload)
                    }
                    keyShareDialog = false
                }) { Text(tx("NFC")) }
            }
        )
    }
    if (keyQrPayload != null && keyQrBitmap != null) {
        AlertDialog(
            onDismissRequest = { keyQrPayload = null; keyQrBitmap = null },
            title = { Text(tx("QR key transfer")) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = keyQrBitmap!!.asImageBitmap(),
                        contentDescription = tx("Key QR"),
                        modifier = Modifier.size(280.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { keyQrPayload = null; keyQrBitmap = null }) { Text(tx("CLOSE")) }
            }
        )
    }
    if (infoDialogText != null) {
        AlertDialog(
            onDismissRequest = { infoDialogText = null },
            title = { Text(tx("Info")) },
            text = { Text(resolveInfoDialogText(infoDialogText ?: "")) },
            confirmButton = {
                TextButton(onClick = { infoDialogText = null }) { Text(tx("OK")) }
            }
        )
    }
    if (generationSuccessDialog != null) {
        val keyCreatedFallback = tx("Key created successfully.")
        AlertDialog(
            onDismissRequest = { generationSuccessDialog = null },
            title = { Text(tx("Success")) },
            text = { Text(generationSuccessDialog?.let { tx(it) } ?: keyCreatedFallback) },
            confirmButton = {
                TextButton(onClick = {
                    generationSuccessDialog = null
                    scope.launch { settingsScroll.animateScrollTo(settingsScroll.maxValue) }
                }) { Text(tx("OK")) }
            }
        )
    }
    if (confirmDeleteKeyId != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteKeyId = null },
            title = { Text(tx("Delete key?")) },
            text = { Text(tx("Are you sure you want to delete this key? This action cannot be undone.")) },
            confirmButton = {
                TextButton(onClick = {
                    val keyId = confirmDeleteKeyId
                    confirmDeleteKeyId = null
                    if (keyId != null) onDeleteSharedKey(keyId)
                }) { Text(tx("YES"), color = Ember) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteKeyId = null }) { Text(tx("NO")) }
            }
        )
    }
    if (confirmDeleteBackupId != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteBackupId = null },
            title = { Text(tx("Delete backup record?")) },
            text = { Text(tx("Are you sure you want to delete this saved backup record? This action cannot be undone.")) },
            confirmButton = {
                TextButton(onClick = {
                    val backupId = confirmDeleteBackupId
                    confirmDeleteBackupId = null
                    if (backupId != null) onDeleteSavedBackup(backupId)
                }) { Text(tx("YES"), color = Ember) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteBackupId = null }) { Text(tx("NO")) }
            }
        )
    }
}

@Composable
private fun PurchaseScreen(
    state: UiState,
    onBack: () -> Unit,
    onBuyRemoveAds: () -> Unit,
    onBuyProFeatures: () -> Unit,
    onRestorePurchases: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
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
            Text(tx("Purchase options"), style = MaterialTheme.typography.titleLarge, color = onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(tx("One-time products from Google Play."),
                style = MaterialTheme.typography.bodySmall,
                color = onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tx("Remove Ads (Lifetime)"), style = MaterialTheme.typography.titleMedium, color = onSurface)
                    Text(tx("Permanently removes banner and rewarded ad prompts."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (state.isAdFree) "Owned" else state.removeAdsPrice,
                        color = if (state.isAdFree) Moss else Brass,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onBuyRemoveAds,
                        enabled = state.billingReady && !state.isAdFree,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.isAdFree) "OWNED" else "BUY REMOVE ADS")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(tx("Pro Features (Lifetime)"), style = MaterialTheme.typography.titleMedium, color = onSurface)
                    Text(tx("Unlocks unlimited share credits. Does not remove ads. Backup and more are coming soon."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        if (state.hasProFeatures) "Owned" else state.proFeaturesPrice,
                        color = if (state.hasProFeatures) Moss else Brass,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onBuyProFeatures,
                        enabled = state.billingReady && !state.hasProFeatures,
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (state.hasProFeatures) "OWNED" else "BUY PRO FEATURES")
                    }
                }
            }

            if (!state.billingReady) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(tx("Google Play Billing is not ready yet. Please wait a moment and try again."),
                    style = MaterialTheme.typography.bodySmall,
                    color = Ember
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onRestorePurchases, modifier = Modifier.fillMaxWidth()) {
                Text(tx("RESTORE PURCHASES"), color = Brass)
            }

            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(tx("BACK TO SETTINGS"), color = Brass)
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
private fun OnboardingScreen(
    languageTag: String,
    onSelectLanguage: (String) -> Unit,
    onComplete: () -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.Shield,
            title = tx("Welcome to Nulvex"),
            description = tx("Your offline-first secure vault. No cloud, no tracking, no compromise.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Security,
            title = tx("Military-grade encryption"),
            description = tx("Your notes are encrypted with XChaCha20-Poly1305 and post-quantum ready key exchange.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Schedule,
            title = tx("Self-destruct notes"),
            description = tx("Set notes to auto-delete after 1 hour, 24 hours, or 7 days. Or make them read-once.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Warning,
            title = tx("Panic button"),
            description = tx("Instantly wipe everything if needed. Optional decoy vault for plausible deniability.")
        ),
        OnboardingPage(
            icon = Icons.Filled.Fingerprint,
            title = tx("Biometric unlock"),
            description = tx("Use your fingerprint to unlock quickly. Your PIN remains the master key.")
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
            Text(
                text = tx("Choose language"),
                style = MaterialTheme.typography.labelLarge,
                color = onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(tx("System"), languageTag == "system") { onSelectLanguage("system") }
                Chip(tx("English"), languageTag == "en") { onSelectLanguage("en") }
                Chip(tx("Serbian"), languageTag == "sr") { onSelectLanguage("sr") }
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                        Text(tx("BACK"), color = onSurface.copy(alpha = 0.7f))
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
                        Text(tx("NEXT"))
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand)
                    ) {
                        Text(tx("GET STARTED"))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = onComplete) {
                    Text(tx("SKIP"), color = onSurface.copy(alpha = 0.5f))
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

@Composable
private fun formatExpiryBadge(expiresAt: Long): String {
    val remaining = expiresAt - System.currentTimeMillis()
    if (remaining <= 0L) return "${tx("Next expiry:")} ${tx("overdue")}"
    val minutes = max(1, remaining / 60_000L)
    val hours = remaining / 3_600_000L
    val days = remaining / 86_400_000L
    return when {
        days >= 1 -> "${tx("Next expiry:")} ${days}d"
        hours >= 1 -> "${tx("Next expiry:")} ${hours}h"
        else -> "${tx("Next expiry:")} ${minutes}m"
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
        IconButton(onClick = onCreate) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = tx("New note"),
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
        Chip(tx("All"), activeLabel == null) { onSelectLabel(null) }
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
                        text = tx("EXPIRING"),
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
            Text(tx("X"), color = Moss, style = MaterialTheme.typography.labelLarge)
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
                text = tx("No matches"),
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

private fun generateQrBitmap(content: String, size: Int): Bitmap? {
    return try {
        val matrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
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
    onCancel: () -> Unit,
    defaultExpiry: String,
    defaultReadOnce: Boolean,
    onDraftChanged: (NewNoteDraft?) -> Unit = {}
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    var content by remember { mutableStateOf("") }
    var readOnce by remember(defaultReadOnce) { mutableStateOf(defaultReadOnce) }
    var expiryChoice by remember(defaultExpiry) { mutableStateOf(defaultExpiry) }
    var customExpiresAt by remember { mutableStateOf<Long?>(null) }
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

    LaunchedEffect(content, checklistItems, labels, pinned, expiryChoice, customExpiresAt, readOnce) {
        val expiresAtMs = when (expiryChoice) {
            "1h" -> System.currentTimeMillis() + 3_600_000L
            "24h" -> System.currentTimeMillis() + 86_400_000L
            "7d" -> System.currentTimeMillis() + 604_800_000L
            "custom" -> customExpiresAt
            else -> null
        }
        onDraftChanged(NewNoteDraft(content, checklistItems, labels, pinned, expiresAtMs, readOnce))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tx("New note"), style = MaterialTheme.typography.titleLarge, color = onSurface, modifier = Modifier.weight(1f))
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
                    Text(tx("ADD"))
                }
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(tx("Checklist")) },
                        onClick = {
                            showChecklist = true
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
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(tx("Write safely...")) },
                minLines = 10,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 260.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = pinned, onCheckedChange = { pinned = it })
                Text(tx("Pin to top"), color = onSurface)
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (showChecklist) {
                Text(tx("Checklist"), style = MaterialTheme.typography.labelLarge, color = onSurface)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newChecklistItem,
                        onValueChange = { newChecklistItem = it },
                        label = { Text(tx("Add item")) },
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
                        Text(tx("ADD"))
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
                        Text(tx("ADD IMAGE"))
                    }
                    if (attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(tx("${attachments.size} attached"), color = onSurface.copy(alpha = 0.7f))
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
                        Text(tx("ADD"))
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
                    tx("Selected expiry:") + " " + dateTimeFormatter.format(java.util.Date(customExpiresAt!!)),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
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
                    Text(tx("CANCEL"))
                }
                Button(
                    onClick = { onCreate(content, checklistItems, labels, pinned, attachments, expiresAt, readOnce) },
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(tx("SAVE NOTE"))
                }
            }
        }
    }
}

@Composable
private fun NoteDetailScreen(
    state: UiState,
    onClose: () -> Unit,
    onUpdateNoteText: (String, String, Long?) -> Unit,
    onShareNote: (String) -> Unit,
    onDelete: (String) -> Unit,
    onTogglePinned: (String) -> Unit,
    onToggleChecklistItem: (String, String) -> Unit,
    onAddChecklistItem: (String, String) -> Unit,
    onRemoveChecklistItem: (String, String) -> Unit,
    onUpdateChecklistText: (String, String, String) -> Unit,
    onMoveChecklistItem: (String, String, Int) -> Unit,
    onLoadAttachmentPreview: (String, String) -> Unit,
    onRemoveAttachment: (String, String) -> Unit,
    onNoteEditDraftChanged: (String, String, Long?) -> Unit = { _, _, _ -> },
    onClearNoteEditDraft: () -> Unit = {}
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val note = state.selectedNote
    if (note == null) {
        Text(tx("Note unavailable"), color = onSurface)
        return
    }
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember(note.id) { mutableStateOf(note.text) }
    var expiryChoice by remember(note.id) {
        mutableStateOf(if (note.expiresAt == null) "none" else "custom")
    }
    var customExpiresAt by remember(note.id) {
        mutableStateOf(note.expiresAt)
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
    val context = LocalContext.current
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tx("Note"),
                    style = MaterialTheme.typography.titleLarge,
                    color = onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (isEditing) {
                        isEditing = false
                        editText = note.text
                        customExpiresAt = note.expiresAt
                        expiryChoice = if (note.expiresAt == null) "none" else "custom"
                        onClearNoteEditDraft()
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
                TextButton(onClick = { onShareNote(note.id) }) {
                    Text(tx("SHARE LINK"), color = Moss)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (isEditing) {
                OutlinedTextField(
                    value = editText,
                    onValueChange = {
                        editText = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                        autoCorrectEnabled = false
                    )
                )
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
                        tx("Selected expiry:") + " " + dateTimeFormatter.format(java.util.Date(customExpiresAt!!)),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else if (note.text.isNotBlank()) {
                Text(note.text, color = onSurface, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (note.checklist.isNotEmpty()) {
                Text(tx("Checklist"), color = onSurface, style = MaterialTheme.typography.labelLarge)
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
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                onUpdateChecklistText(note.id, item.id, editingChecklistText)
                                editingChecklistId = null
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = tx("Save item"),
                                    tint = Brass
                                )
                            }
                            IconButton(onClick = { editingChecklistId = null }) {
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
            } else {
                Text(tx("Checklist"), color = onSurface, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(6.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = checklistInput,
                    onValueChange = { checklistInput = it },
                    label = { Text(tx("Add item")) },
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
                    Text(tx("ADD"))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (note.attachments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(tx("Attachments"), color = onSurface, style = MaterialTheme.typography.labelLarge)
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
                            onUpdateNoteText(note.id, editText, editedExpiresAt)
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text(tx("SAVE"))
                    }
                    TextButton(onClick = {
                        isEditing = false
                        editText = note.text
                        customExpiresAt = note.expiresAt
                        expiryChoice = if (note.expiresAt == null) "none" else "custom"
                        onClearNoteEditDraft()
                    }) {
                        Text(tx("CANCEL"), color = onSurface.copy(alpha = 0.7f))
                    }
                } else {
                    Button(
                        onClick = { onDelete(note.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Ember, contentColor = Sand)
                    ) {
                        Text(tx("DELETE"))
                    }
                    TextButton(onClick = onClose) {
                        Text(tx("BACK"), color = Brass)
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
private fun PendingImportDialog(
    state: UiState,
    onImportFile: (ByteArray, String, String, Boolean) -> Unit,
    onImportKeyManager: (ByteArray, String?) -> Unit,
    onImportRemote: (String, String, Boolean) -> Unit,
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

    val isKeysFile = import is PendingImport.LocalFile &&
        (import as PendingImport.LocalFile).mimeType == com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME
    val isNoteShare = import is PendingImport.LocalFile &&
        (import as PendingImport.LocalFile).mimeType == com.androidircx.nulvex.pro.NulvexFileTypes.NOTE_SHARE_MIME
    val noKeys = state.sharedKeys.isEmpty() && !isKeysFile

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when {
                    isKeysFile -> tx("Import Keys")
                    isNoteShare -> tx("Import Note")
                    import is PendingImport.RemoteMedia -> tx("Import from Link")
                    else -> tx("Import Backup")
                },
                color = onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (import is PendingImport.RemoteMedia) {
                    Text(
                        tx("Media ID:") + " " + import.mediaId.take(16) + "…",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.6f)
                    )
                }
                if (isKeysFile) {
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
                                    text = { Text(key.label) },
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
                    when {
                        isKeysFile && import is PendingImport.LocalFile ->
                            onImportKeyManager(import.bytes, password.ifBlank { null })
                        import is PendingImport.LocalFile ->
                            onImportFile(import.bytes, import.mimeType, selectedKeyId, mergeMode || isNoteShare)
                        import is PendingImport.RemoteMedia ->
                            onImportRemote(import.mediaId, selectedKeyId, mergeMode)
                        else -> onDismiss()
                    }
                },
                enabled = !state.isBusy && (isKeysFile || !noKeys),
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
private fun ErrorBar(state: UiState, onClear: () -> Unit) {
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
private fun localizeRuntimeMessage(msg: String): String {
    val lockoutMatch = Regex("^Too many attempts\\. Try again in (\\d+)s$").matchEntire(msg)
    if (lockoutMatch != null) {
        val seconds = lockoutMatch.groupValues[1]
        return tx("Too many attempts. Try again in {seconds}s").replace("{seconds}", seconds)
    }
    return tx(msg)
}

@Composable
private fun resolveInfoDialogText(key: String): String {
    return when (key) {
        "info_keys_manager_overview" -> tx("Keys Manager stores keys used for encrypted note sharing and backups.") + "\n\n" +
            tx("- OpenPGP key: generated/imported PGP key material.") + "\n" +
            tx("- XChaCha key: 32-byte symmetric key used for fast encrypted payload exchange.") + "\n\n" +
            tx("Sources (manual/qr/nfc) are auto-tagged based on how key was imported.")
        "info_manual_import" -> tx("Manual import accepts:") + "\n\n" +
            tx("1) OpenPGP armored key blocks (BEGIN PGP ... END PGP)") + "\n" +
            tx("2) XChaCha key as:") + "\n" +
            tx("- base64 string decoding to exactly 32 bytes, or") + "\n" +
            tx("- 64-char hex string (32 bytes).")
        "info_generate_help" -> tx("Generate XChaCha creates a new random 32-byte symmetric key.") + "\n\n" +
            tx("Generate PGP creates a new OpenPGP key pair stored in the app key vault.")
        "info_qr_nfc_exchange" -> tx("SHARE SELECTED KEY lets you transfer a selected key to another Nulvex user.") + "\n\n" +
            tx("- QR: show code on screen for scan.") + "\n" +
            tx("- NFC: writes key payload to NFC tag.") + "\n\n" +
            tx("Receiver imports it via QR scanner or NFC read.")
        "info_backup_modes" -> tx("Backup modes:") + "\n\n" +
            tx("- Local backup: exports encrypted file to your phone storage.") + "\n" +
            tx("- Remote backup (Pro): uploads encrypted blob to your media server.") + "\n\n" +
            tx("In both cases, decrypt requires the correct key.")
        else -> tx(key)
    }
}

@Composable
private fun LabelsMenu(
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
                                contentDescription = if (hasLabel) "Remove label" else "Add label",
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

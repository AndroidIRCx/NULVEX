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
import androidx.compose.runtime.LaunchedEffect
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
internal fun Modifier.bringIntoViewOnFocus(): Modifier {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    return this
        .bringIntoViewRequester(bringIntoViewRequester)
        .onFocusEvent { focusState ->
            if (focusState.isFocused) {
                scope.launch {
                    delay(180)
                    bringIntoViewRequester.bringIntoView()
                }
            }
        }
}

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
    onToggleAutoBiometricPrompt: (Boolean) -> Unit = {},
    onRequestDecoyBiometricEnroll: (String) -> Unit = {},
    onRequestDecoyBiometricUnlock: () -> Unit = {},
    onDisableDecoyBiometric: () -> Unit = {},
    onTogglePinScramble: (Boolean) -> Unit = {},
    onToggleHidePinLength: (Boolean) -> Unit = {},
    onChangeRealPin: (String, String, String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateThemePalette: (String) -> Unit = {},
    onToggleDynamicColor: (Boolean) -> Unit = {},
    onSaveCustomTheme: (com.androidircx.nulvex.ui.theme.ThemePalette) -> Unit = {},
    onDeleteCustomTheme: (String) -> Unit = {},
    onUpdateLanguage: (String) -> Unit = {},
    onOpenNew: () -> Unit,
    onQuickCreate: (QuickCreateType) -> Unit = {},
    onCreate: (String, String, List<ChecklistItem>, List<String>, Boolean, List<android.net.Uri>, Long?, Boolean, Long?, String?) -> Unit,
    onOpenNote: (String) -> Unit,
    onOpenLinkedNote: (String) -> Unit = {},
    onToggleNoteSelection: (String) -> Unit = {},
    onClearNoteSelection: () -> Unit = {},
    onBulkArchiveSelected: () -> Unit = {},
    onBulkDeleteSelected: () -> Unit = {},
    onBulkAddLabelSelected: (String) -> Unit = {},
    onBulkSetReminderSelected: (Long) -> Unit = {},
    onCloseNote: () -> Unit,
    onUpdateNoteText: (String, String, Long?) -> Unit,
    onSaveEditedNote: (String, String, String, List<String>, List<android.net.Uri>, Long?) -> Unit = { _, _, _, _, _, _ -> },
    onShareNote: (String) -> Unit = {},
    onExportNoteFile: (String) -> Unit = {},
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
    onSetShowArchived: (Boolean) -> Unit = {},
    onSetShowTrash: (Boolean) -> Unit = {},
    onLoadAttachmentPreview: (String, String) -> Unit,
    onRemoveAttachment: (String, String) -> Unit,
    onExportAttachment: (String, String, String) -> Unit = { _, _, _ -> },
    onToggleArchived: (String) -> Unit = {},
    onRestoreNoteFromTrash: (String) -> Unit = {},
    onSetNoteReminder: (String, Long) -> Unit = { _, _ -> },
    onSetNoteReminderRepeat: (String, String?) -> Unit = { _, _ -> },
    onClearNoteReminder: (String) -> Unit = {},
    onRestoreNoteRevision: (String, String) -> Unit = { _, _ -> },
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
    onUploadKeyManagerToApi: (Boolean, String?) -> Unit = { _, _ -> },
    onRestoreKeyManagerFromApi: (String, String?) -> Unit = { _, _ -> },
    onGenerateXChaChaKey: (String) -> Unit = {},
    onGeneratePgpKey: (String) -> Unit = {},
    onBuildKeyTransferPayload: (String) -> String? = { null },
    onBuildQrKeyTransferPayload: (String) -> String? = { null },
    onStartNfcKeyShare: (String) -> Unit = {},
    onNoteEditDraftChanged: (String, String, Long?) -> Unit = { _, _, _ -> },
    onClearNoteEditDraft: () -> Unit = {},
    onUndoNoteEdit: (String) -> Unit = {},
    onRedoNoteEdit: (String) -> Unit = {},
    onNewNoteDraftChanged: (NewNoteDraft?) -> Unit = {},
    onImportIncomingFile: (ByteArray, String, String, Boolean) -> Unit = { _, _, _, _ -> },
    onImportIncomingKeyManager: (ByteArray, String?) -> Unit = { _, _ -> },
    onImportIncomingRemote: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onImportIncomingRemoteKeyManager: (String, String?) -> Unit = { _, _ -> },
    onClearPendingImport: () -> Unit = {},
    onClearNoteShareUrl: () -> Unit = {},
    onResolveSyncConflict: (String) -> Unit = {},
    onClearKeyRotationState: () -> Unit = {}
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
                            onQuickCreate = onQuickCreate,
                            onOpenNote = onOpenNote,
                            onToggleNoteSelection = onToggleNoteSelection,
                            onClearNoteSelection = onClearNoteSelection,
                            onBulkArchiveSelected = onBulkArchiveSelected,
                            onBulkDeleteSelected = onBulkDeleteSelected,
                            onBulkAddLabelSelected = onBulkAddLabelSelected,
                            onBulkSetReminderSelected = onBulkSetReminderSelected,
                            onTogglePinned = onTogglePinned,
                            onDelete = onDelete,
                            onSearchQueryChange = onSearchQueryChange,
                            onSetShowArchived = onSetShowArchived,
                            onSetShowTrash = onSetShowTrash
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
                            onToggleAutoBiometricPrompt = onToggleAutoBiometricPrompt,
                            onRequestBiometricEnroll = onRequestBiometricEnroll,
                            onRequestDecoyBiometricEnroll = onRequestDecoyBiometricEnroll,
                            onDisableDecoyBiometric = onDisableDecoyBiometric,
                            onTogglePinScramble = onTogglePinScramble,
                            onToggleHidePinLength = onToggleHidePinLength,
                            onChangeRealPin = onChangeRealPin,
                            onUpdateThemeMode = onUpdateThemeMode,
                            onUpdateThemePalette = onUpdateThemePalette,
                            onToggleDynamicColor = onToggleDynamicColor,
                            onSaveCustomTheme = onSaveCustomTheme,
                            onDeleteCustomTheme = onDeleteCustomTheme,
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
                            onUploadKeyManagerToApi = onUploadKeyManagerToApi,
                            onRestoreKeyManagerFromApi = onRestoreKeyManagerFromApi,
                            onGenerateXChaChaKey = onGenerateXChaChaKey,
                            onGeneratePgpKey = onGeneratePgpKey,
                            onBuildKeyTransferPayload = onBuildKeyTransferPayload,
                            onBuildQrKeyTransferPayload = onBuildQrKeyTransferPayload,
                            onStartNfcKeyShare = onStartNfcKeyShare,
                            onResolveSyncConflict = onResolveSyncConflict,
                            onClearKeyRotationState = onClearKeyRotationState
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
                            onOpenLinkedNote,
                            onUpdateNoteText,
                            onSaveEditedNote,
                            onShareNote,
                            onExportNoteFile,
                            onDelete,
                            onTogglePinned,
                            onToggleArchived,
                            onRestoreNoteFromTrash,
                            onSetNoteReminder,
                            onSetNoteReminderRepeat,
                            onClearNoteReminder,
                            onRestoreNoteRevision,
                            onToggleChecklistItem,
                            onAddChecklistItem,
                            onRemoveChecklistItem,
                            onUpdateChecklistText,
                            onMoveChecklistItem,
                            onLoadAttachmentPreview,
                            onRemoveAttachment,
                            onExportAttachment,
                            onNoteEditDraftChanged,
                            onClearNoteEditDraft,
                            onUndoNoteEdit,
                            onRedoNoteEdit
                        )
                    }
                }
                } // end inner padding Column
            }
        }
        ErrorBar(state, onClearError)
        if (state.noteShareUrl.isNotBlank()) {
            val clipboard = androidx.compose.ui.platform.LocalClipboard.current
            val scope = rememberCoroutineScope()
            AlertDialog(
                onDismissRequest = onClearNoteShareUrl,
                title = { Text(tx("Note uploaded")) },
                text = {
                    Column {
                        Text(tx("Your encrypted note is available at:"), style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.noteShareUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            clipboard.setClipEntry(
                                androidx.compose.ui.platform.ClipEntry(
                                    ClipData.newPlainText("note_share_url", state.noteShareUrl)
                                )
                            )
                        }
                        onClearNoteShareUrl()
                    }) { Text(tx("COPY LINK")) }
                },
                dismissButton = {
                    TextButton(onClick = onClearNoteShareUrl) { Text(tx("CLOSE")) }
                }
            )
        }
        if (state.pendingImport != null && state.screen == Screen.Vault) {
            PendingImportDialog(
                state = state,
                onImportFile = onImportIncomingFile,
                onImportKeyManager = onImportIncomingKeyManager,
                onImportRemote = onImportIncomingRemote,
                onImportRemoteKeyManager = onImportIncomingRemoteKeyManager,
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
    val candidate = if (trimmed.contains(marker)) {
        trimmed.substringAfter(marker).substringBefore("?").substringBefore("#").trim().trim('/')
    } else {
        trimmed
    }
    val normalized = candidate.trim()
    if (normalized.isBlank()) return ""
    if (normalized.contains("..")) return ""
    if (normalized.contains("/") || normalized.contains("\\")) return ""
    if (!Regex("^[A-Za-z0-9._-]{1,200}$").matches(normalized)) return ""
    return normalized
}


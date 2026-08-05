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
internal fun SettingsScreen(
    state: UiState,
    decoyVisible: Boolean = false,
    onUpdateDecoyPin: (String, String) -> Unit,
    onDisableDecoy: () -> Unit,
    onUpdateLockTimeout: (Long) -> Unit,
    onUpdateDefaultExpiry: (String) -> Unit,
    onUpdateDefaultReadOnce: (Boolean) -> Unit,
    onDisableBiometric: () -> Unit,
    onToggleAutoBiometricPrompt: (Boolean) -> Unit = {},
    onRequestBiometricEnroll: (String) -> Unit,
    onRequestDecoyBiometricEnroll: (String) -> Unit = {},
    onDisableDecoyBiometric: () -> Unit = {},
    onTogglePinScramble: (Boolean) -> Unit = {},
    onToggleHidePinLength: (Boolean) -> Unit = {},
    onChangeRealPin: (String, String, String) -> Unit,
    onUpdateThemeMode: (ThemeMode) -> Unit,
    onUpdateThemePalette: (String) -> Unit = {},
    onToggleDynamicColor: (Boolean) -> Unit = {},
    onSaveCustomTheme: (com.androidircx.nulvex.ui.theme.ThemePalette) -> Unit = {},
    onDeleteCustomTheme: (String) -> Unit = {},
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
    onUploadKeyManagerToApi: (Boolean, String?) -> Unit = { _, _ -> },
    onRestoreKeyManagerFromApi: (String, String?) -> Unit = { _, _ -> },
    onGenerateXChaChaKey: (String) -> Unit = {},
    onGeneratePgpKey: (String) -> Unit = {},
    onBuildKeyTransferPayload: (String) -> String? = { null },
    onBuildQrKeyTransferPayload: (String) -> String? = { null },
    onStartNfcKeyShare: (String) -> Unit = {},
    onResolveSyncConflict: (String) -> Unit = {},
    onClearKeyRotationState: () -> Unit = {}
) {
    val context = LocalContext.current
    val onSurface = MaterialTheme.colorScheme.onSurface
    val clipboard = androidx.compose.ui.platform.LocalClipboard.current
    val scope = rememberCoroutineScope()
    var decoyPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var decoyBiometricPin by remember { mutableStateOf("") }
    val pinMismatch = decoyPin.isNotEmpty() && confirmPin.isNotEmpty() && decoyPin != confirmPin
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }
    var biometricPin by remember { mutableStateOf("") }
    var fingerprintTargetVault by remember { mutableStateOf("real") }
    val realPinMismatch = newPin.isNotEmpty() && confirmNewPin.isNotEmpty() && newPin != confirmNewPin
    var settingsSearch by remember { mutableStateOf("") }
    var expandedSections by remember { mutableStateOf(setOf<String>()) }
    var keyLabel by remember { mutableStateOf("") }
    var keyInput by remember { mutableStateOf("") }
    var selectedKeyId by remember { mutableStateOf(state.sharedKeys.firstOrNull()?.id ?: "") }
    var restoreMediaId by remember { mutableStateOf(state.lastBackupMediaId) }
    var selectedBackupRecordId by remember { mutableStateOf("") }
    var restoreMerge by remember { mutableStateOf(true) }
    var keyManagerExportEncrypted by remember { mutableStateOf(true) }
    var keyManagerPassword by remember { mutableStateOf("") }
    var keyManagerImportPassword by remember { mutableStateOf("") }
    var keyManagerApiRestoreId by remember { mutableStateOf("") }
    var keyShareDialog by remember { mutableStateOf(false) }
    var keyQrPayload by remember { mutableStateOf<String?>(null) }
    var keyQrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var infoDialogText by remember { mutableStateOf<String?>(null) }
    var generationSuccessDialog by remember { mutableStateOf<String?>(null) }
    var confirmDeleteKeyId by remember { mutableStateOf<String?>(null) }
    var confirmDeleteBackupId by remember { mutableStateOf<String?>(null) }
    var lastHandledStatus by remember { mutableStateOf("") }
    var showKeyRotationWizard by remember { mutableStateOf(false) }
    var krOldPin by remember { mutableStateOf("") }
    var krNewPin by remember { mutableStateOf("") }
    var krConfirmPin by remember { mutableStateOf("") }
    val settingsScroll = rememberScrollState()
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
        tx("Rewards & Ads"),
        tx("Remove ads and earn share credits"),
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
    val showDisplay = matchesSection(
        tx("Display"),
        tx("Appearance and theme"),
        "display",
        "theme",
        "appearance",
        "dark",
        "light",
        "system"
    )
    val showVaultDefaults = matchesSection(
        tx("Vault defaults"),
        tx("Auto-lock and note behavior"),
        "vault defaults",
        "auto-lock",
        "timeout",
        "self-destruct",
        "expiry",
        "read-once"
    )
    val showSecurity = matchesSection(
        tx("Security"),
        tx("Authentication and encryption"),
        "security",
        "fingerprint",
        "biometric",
        "pin",
        "change primary pin",
        "encryption"
    )
    val showDanger = decoyVisible && matchesSection(
        tx("Danger zone"),
        tx("Decoy vault and destructive actions"),
        "danger zone",
        "decoy",
        "decoy pin",
        "wipe",
        "coercion",
        "plausible deniability"
    )
    val showKeys = matchesSection(
        tx("Keys Manager"),
        tx("OpenPGP + XChaCha key storage"),
        "keys",
        "pgp",
        "xchacha",
        "nfc",
        "qr",
        "key manager"
    )
    val showBackup = matchesSection(
        tx("Backup"),
        tx("Local encrypted backup + Pro remote encrypted storage"),
        "backup",
        "restore",
        "encrypted backup",
        "local backup",
        "remote encrypted storage"
    )
    val showAbout = matchesSection(
        tx("About"),
        tx("Offline-first secure vault"),
        "about",
        "version",
        "nulvex",
        "offline",
        "xchacha20",
        "kyber768"
    )
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
            generationSuccessDialog = context.tx("Key created successfully.")
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
                                if (adFreeActive) {
                                    tx("{time} remaining").replace("{time}", formatRemaining(remainingMs))
                                } else {
                                    tx("Ads are active")
                                },
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
                            Text(tx("Used to share notes via remote encrypted storage"),
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
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        Chip(tx("System"), state.themeMode == ThemeMode.SYSTEM) { onUpdateThemeMode(ThemeMode.SYSTEM) }
                        Chip(tx("Dark"), state.themeMode == ThemeMode.DARK) { onUpdateThemeMode(ThemeMode.DARK) }
                        Chip(tx("Light"), state.themeMode == ThemeMode.LIGHT) { onUpdateThemeMode(ThemeMode.LIGHT) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ThemePaletteSection(
                        state = state,
                        onUpdateThemePalette = onUpdateThemePalette,
                        onToggleDynamicColor = onToggleDynamicColor,
                        onSaveCustomTheme = onSaveCustomTheme,
                        onDeleteCustomTheme = onDeleteCustomTheme
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.settings_language_title), style = MaterialTheme.typography.labelLarge, color = onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
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
                    stringResource(R.string.settings_lock_timeout_off) to 0L,
                    stringResource(R.string.settings_lock_timeout_30s) to 30_000L,
                    stringResource(R.string.settings_lock_timeout_1m) to 60_000L,
                    stringResource(R.string.settings_lock_timeout_5m) to 300_000L,
                    stringResource(R.string.settings_lock_timeout_10m) to 600_000L
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
                    val anyBiometricEnabled = state.biometricEnabled || state.decoyBiometricEnabled
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = null,
                        tint = if (anyBiometricEnabled) Moss else onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx("Fingerprint unlock"), color = onSurface)
                        Text(
                            if (anyBiometricEnabled) tx("Enabled") else tx("Disabled"),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (anyBiometricEnabled) Moss else onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                val activeBiometricEnabled = when (state.biometricTargetVault) {
                    "decoy" -> state.decoyBiometricEnabled
                    else -> state.biometricEnabled
                }
                if (activeBiometricEnabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val vaultLabel = if (state.biometricTargetVault == "decoy") tx("Decoy vault") else tx("Main vault")
                    Text(
                        tx("Opens: {vault}").replace("{vault}", vaultLabel),
                        style = MaterialTheme.typography.bodySmall,
                        color = Moss
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = if (state.biometricTargetVault == "decoy") onDisableDecoyBiometric else onDisableBiometric,
                        enabled = !state.isBusy
                    ) {
                        Text(tx("DISABLE FINGERPRINT"), color = Ember)
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.isDecoyEnabled) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = fingerprintTargetVault == "real",
                                onClick = { fingerprintTargetVault = "real" },
                                label = { Text(tx("Main vault")) }
                            )
                            FilterChip(
                                selected = fingerprintTargetVault == "decoy",
                                onClick = { fingerprintTargetVault = "decoy" },
                                label = { Text(tx("Decoy vault")) }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    OutlinedTextField(
                        value = biometricPin,
                        onValueChange = { biometricPin = it },
                        label = {
                            Text(
                                if (fingerprintTargetVault == "decoy") tx("Decoy PIN to enable")
                                else tx("Current PIN to enable")
                            )
                        },
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
                            if (fingerprintTargetVault == "decoy") {
                                onRequestDecoyBiometricEnroll(biometricPin)
                            } else {
                                onRequestBiometricEnroll(biometricPin)
                            }
                            biometricPin = ""
                        },
                        enabled = !state.isBusy && biometricPin.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink)
                    ) {
                        Text(tx("ENABLE FINGERPRINT"))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleAutoBiometricPrompt(!state.autoBiometricPromptEnabled) }
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx("Auto-show fingerprint on app open"), color = onSurface)
                        Text(
                            tx("When enabled, unlock prompt appears immediately on the unlock screen"),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = state.autoBiometricPromptEnabled,
                        onCheckedChange = onToggleAutoBiometricPrompt
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // PIN Scramble
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTogglePinScramble(!state.pinScrambleEnabled) }
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx("Scramble PIN pad"), color = onSurface)
                        Text(
                            tx("Randomly shuffles digits on each unlock to prevent fingerprint tracing"),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = state.pinScrambleEnabled,
                        onCheckedChange = onTogglePinScramble
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hide PIN length
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleHidePinLength(!state.hidePinLengthEnabled) }
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx("Hide PIN length"), color = onSurface)
                        Text(
                            tx("Always shows 6 dots regardless of actual PIN length"),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = state.hidePinLengthEnabled,
                        onCheckedChange = onToggleHidePinLength
                    )
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

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                // F3 – Key Rotation Wizard
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = Moss,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx("Key Rotation Wizard"), color = onSurface)
                        Text(
                            tx("Guided re-encryption of vault with new PIN"),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        krOldPin = ""
                        krNewPin = ""
                        krConfirmPin = ""
                        onClearKeyRotationState()
                        showKeyRotationWizard = true
                    },
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Ink)
                ) {
                    Text(tx("OPEN KEY ROTATION WIZARD"))
                }

                // F2 – Security Event Timeline
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(tx("Security Timeline"), color = onSurface)
                        Text(
                            tx("Local audit log — unlock, wipe, key events"),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                if (state.securityEvents.isEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        tx("No events recorded yet."),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.5f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    state.securityEvents.take(30).forEach { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val (icon, color) = when (event.type) {
                                SecurityEventStore.EVENT_UNLOCK_SUCCESS -> Icons.Filled.Shield to Moss
                                SecurityEventStore.EVENT_UNLOCK_FAIL -> Icons.Filled.Warning to Ember
                                SecurityEventStore.EVENT_LOCKOUT -> Icons.Filled.Lock to Ember
                                SecurityEventStore.EVENT_PANIC_WIPE -> Icons.Filled.DeleteForever to Ember
                                SecurityEventStore.EVENT_KEY_ROTATION -> Icons.Filled.Security to Brass
                                SecurityEventStore.EVENT_BACKUP_EXPORT -> Icons.Filled.FileDownload to Brass
                                else -> Icons.Filled.Info to onSurface.copy(alpha = 0.5f)
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 0.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    event.type.replace('_', ' '),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onSurface
                                )
                                if (event.detail.isNotBlank()) {
                                    Text(
                                        event.detail,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = onSurface.copy(alpha = 0.5f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(
                                formatEventTime(event.timestampMillis),
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
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
                    tx("A decoy vault opens when you enter a different PIN. Use it for plausible deniability under coercion."),
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
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = tx("What is Keys Manager?"), tint = Brass)
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
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = tx("Manual format help"), tint = Moss)
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
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = tx("Generate help"), tint = Moss)
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
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = tx("QR/NFC help"), tint = Moss)
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
                if (state.hasProFeatures) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onUploadKeyManagerToApi(
                                keyManagerExportEncrypted,
                                keyManagerPassword.ifBlank { null }
                            )
                        },
                        enabled = !state.isBusy && (!keyManagerExportEncrypted || keyManagerPassword.isNotBlank()),
                        colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Sand),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(tx("UPLOAD KEYS TO REMOTE ENCRYPTED STORAGE")) }
                }

                Spacer(modifier = Modifier.height(12.dp))
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
                if (state.hasProFeatures) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = keyManagerApiRestoreId,
                        onValueChange = { keyManagerApiRestoreId = it },
                        label = { Text(tx("Remote encrypted storage link or ID")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onRestoreKeyManagerFromApi(
                                resolveRemoteMediaIdInput(keyManagerApiRestoreId),
                                keyManagerImportPassword.ifBlank { null }
                            )
                        },
                        enabled = !state.isBusy && resolveRemoteMediaIdInput(keyManagerApiRestoreId).isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Brass, contentColor = Ink),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(tx("RESTORE KEYS FROM REMOTE ENCRYPTED STORAGE")) }
                }

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
                                Text(
                                    tx("via {source} - {format} - {fingerprint}")
                                        .replace("{source}", sourceLabel)
                                        .replace("{format}", key.format)
                                        .replace("{fingerprint}", key.fingerprint),
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
                    val localizedStatus = localizeRuntimeMessage(state.backupStatus)
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
                description = tx("Local encrypted backup + Pro remote encrypted storage"),
                expanded = isExpanded("backup"),
                onToggle = { toggleSection("backup") }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        tx("Local backup exports {ext}. Remote encrypted storage uploads (Pro).")
                            .replace("{ext}", com.androidircx.nulvex.pro.NulvexFileTypes.BACKUP_EXT),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        infoDialogText = "info_backup_modes"
                    }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = tx("Backup help"), tint = Brass)
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
                    if (state.hasProFeatures) tx("Pro remote encrypted storage is active") else tx("Remote encrypted storage requires Pro"),
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
                    ) { Text(tx("UPLOAD TO REMOTE ENCRYPTED STORAGE")) }
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
                ) { Text(tx("RESTORE FROM REMOTE ENCRYPTED STORAGE")) }

                if (state.backupRecords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(tx("Saved remote encrypted storage backups"), style = MaterialTheme.typography.labelLarge, color = onSurface)
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
                                scope.launch {
                                    clipboard.setClipEntry(
                                        androidx.compose.ui.platform.ClipEntry(
                                            ClipData.newPlainText(
                                                "backup_download_url",
                                                "https://androidircx.com/api/media/download/${backup.downloadPathId}"
                                            )
                                        )
                                    )
                                }
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
                    val localizedStatus = localizeRuntimeMessage(state.backupStatus)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(localizedStatus, style = MaterialTheme.typography.bodySmall, color = Moss)
                }
            }

            // === SYNC STATUS SECTION (F1) ===
            val showSync = state.hasProFeatures && matchesSection(
                tx("Sync"),
                tx("Multi-device sync status"),
                "sync",
                "conflict",
                "multi-device"
            )
            if (showSync && (showAds || showDisplay || showVaultDefaults || showSecurity || showDanger || showKeys || showBackup)) {
                SettingsDivider()
            }
            if (showSync) SettingsSection(
                icon = Icons.Filled.Schedule,
                title = tx("Sync"),
                description = tx("Multi-device sync status"),
                expanded = isExpanded("sync"),
                onToggle = { toggleSection("sync") }
            ) {
                val lastSyncTs = state.lastSyncAt
                val lastSyncLabel = if (lastSyncTs == 0L) {
                    tx("Never synced")
                } else {
                    val diffSec = (System.currentTimeMillis() - lastSyncTs) / 1000L
                    when {
                        diffSec < 60 -> tx("Synced just now")
                        diffSec < 3600 -> tx("Synced {m}m ago").replace("{m}", (diffSec / 60).toString())
                        diffSec < 86400 -> tx("Synced {h}h ago").replace("{h}", (diffSec / 3600).toString())
                        else -> tx("Synced {d}d ago").replace("{d}", (diffSec / 86400).toString())
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = if (lastSyncTs == 0L) onSurface.copy(alpha = 0.4f) else Moss,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(lastSyncLabel, color = onSurface)
                        if (state.lastSyncConflicts > 0) {
                            Text(
                                tx("{n} conflict(s) last cycle").replace("{n}", state.lastSyncConflicts.toString()),
                                style = MaterialTheme.typography.bodySmall,
                                color = Ember
                            )
                        }
                    }
                }

                if (state.syncConflicts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        tx("Open conflicts — local version was kept"),
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    state.syncConflicts.forEach { conflict ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        tx("Note ID: {id}").replace("{id}", conflict.entityId.take(12) + "…"),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = onSurface
                                    )
                                    Text(
                                        formatEventTime(conflict.createdAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = onSurface.copy(alpha = 0.5f)
                                    )
                                }
                                TextButton(onClick = { onResolveSyncConflict(conflict.id) }) {
                                    Text(tx("DISMISS"), color = Moss)
                                }
                            }
                        }
                    }
                } else if (lastSyncTs > 0L) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        tx("No open conflicts"),
                        style = MaterialTheme.typography.bodySmall,
                        color = Moss
                    )
                }
            }

            if (showAbout && (showAds || showDisplay || showVaultDefaults || showSecurity || showDanger || showKeys || showBackup || showSync)) {
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
                Text(
                    tx("Version {version}").replace("{version}", BuildConfig.VERSION_NAME),
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
                    val payload = onBuildQrKeyTransferPayload(selectedKeyId)
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
        Dialog(onDismissRequest = { keyQrPayload = null; keyQrBitmap = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tx("QR key transfer"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = keyQrBitmap!!.asImageBitmap(),
                        contentDescription = tx("Key QR"),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { keyQrPayload = null; keyQrBitmap = null }) {
                    Text(tx("CLOSE"))
                }
            }
        }
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

    // ── F3: Key Rotation Wizard dialog ───────────────────────────────────────
    if (showKeyRotationWizard) {
        val krPinMismatch = krNewPin.isNotEmpty() && krConfirmPin.isNotEmpty() && krNewPin != krConfirmPin
        Dialog(onDismissRequest = {
            if (!state.isBusy) {
                showKeyRotationWizard = false
                onClearKeyRotationState()
            }
        }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = Moss,
                        modifier = Modifier.padding(end = 10.dp))
                    Text(tx("Key Rotation Wizard"),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    tx("Re-encrypts all notes and the database under your new PIN. This may take a few seconds."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    state.keyRotationDone -> {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = Moss,
                            modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tx("Key rotation complete. Your vault is now re-encrypted with the new PIN."),
                            color = Moss, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                showKeyRotationWizard = false
                                onClearKeyRotationState()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Ink)
                        ) { Text(tx("DONE")) }
                    }
                    state.isBusy -> {
                        Text(tx("Re-encrypting vault…"),
                            color = Brass, style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tx("Please wait, do not close the app."),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    else -> {
                        OutlinedTextField(
                            value = krOldPin,
                            onValueChange = { krOldPin = it },
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
                            value = krNewPin,
                            onValueChange = { krNewPin = it },
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
                            value = krConfirmPin,
                            onValueChange = { krConfirmPin = it },
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
                        if (krPinMismatch) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(tx("PINs do not match"), color = Ember,
                                style = MaterialTheme.typography.labelSmall)
                        }
                        if (state.keyRotationError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(state.keyRotationError, color = Ember,
                                style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    showKeyRotationWizard = false
                                    onClearKeyRotationState()
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text(tx("CANCEL")) }
                            Button(
                                onClick = {
                                    onChangeRealPin(krOldPin, krNewPin, krConfirmPin)
                                },
                                enabled = krOldPin.isNotBlank() && krNewPin.isNotBlank() && !krPinMismatch,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Moss, contentColor = Ink)
                            ) { Text(tx("ROTATE")) }
                        }
                    }
                }
            }
        }
    }
}

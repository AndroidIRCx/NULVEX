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
internal fun AppBackground(content: @Composable () -> Unit) {
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
@Suppress("DEPRECATION")
internal fun BannerAdSection(adUnitId: String, onRemoveAds: () -> Unit) {
    Column {
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    val density = ctx.resources.displayMetrics.density
                    val widthPx = ctx.resources.displayMetrics.widthPixels.toFloat()
                    val adWidthDp = (widthPx / density).toInt()
                    setAdSize(
                        com.google.android.gms.ads.AdSize.getPortraitAnchoredAdaptiveBannerAdSize(ctx, adWidthDp)
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
internal fun TopHeader(
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
internal fun PinKey(label: String, onClick: () -> Unit) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(onSurface.copy(alpha = 0.10f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium,
            color = onSurface,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
internal fun SecurePinPad(
    pin: String,
    label: String,
    onPinChange: (String) -> Unit,
    maxLength: Int = 12,
    scrambled: Boolean = false,
    hidePinLength: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val onSurface = MaterialTheme.colorScheme.onSurface
    val shuffledDigits = remember(scrambled) {
        if (scrambled) (0..9).map { it.toString() }.shuffled()
        else listOf("1","2","3","4","5","6","7","8","9","0")
    }
    val rows = remember(shuffledDigits) {
        listOf(
            listOf(shuffledDigits[0], shuffledDigits[1], shuffledDigits[2]),
            listOf(shuffledDigits[3], shuffledDigits[4], shuffledDigits[5]),
            listOf(shuffledDigits[6], shuffledDigits[7], shuffledDigits[8]),
            listOf("", shuffledDigits[9], "⌫")
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(24.dp)
        ) {
            val dotCount = if (hidePinLength) 6 else maxOf(pin.length, if (pin.isEmpty()) 6 else 0)
            val filledCount = if (hidePinLength) pin.length.coerceAtMost(6) else pin.length
            repeat(dotCount) { i ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(12.dp)
                        .then(
                            if (i < filledCount)
                                Modifier.background(Brass, CircleShape)
                            else
                                Modifier.border(1.5.dp, onSurface.copy(alpha = 0.35f), CircleShape)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
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
internal fun SetupScreen(
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
internal fun UnlockScreen(
    state: UiState,
    onUnlock: (String) -> Unit,
    onRequestBiometricUnlock: () -> Unit,
    onRequestDecoyBiometricUnlock: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    var lockoutRemainingSecs by remember { mutableStateOf(0L) }
    val isLockedOut = lockoutRemainingSecs > 0L

    LaunchedEffect(state.wrongAttempts) {
        if (state.wrongAttempts > 0) pin = ""
    }

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
            Text(tx("Unlock"), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(28.dp))
            SecurePinPad(
                pin = pin,
                label = tx("Enter PIN"),
                onPinChange = { if (!isLockedOut) pin = it },
                scrambled = state.pinScrambleEnabled,
                hidePinLength = state.hidePinLengthEnabled
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
                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tx("UNLOCK"))
                }
            }
            val primaryBiometricReady = when (state.biometricTargetVault) {
                "decoy" -> state.decoyBiometricEnabled
                else -> state.biometricEnabled
            }
            if (primaryBiometricReady && !isLockedOut) {
                val primaryUnlock = when (state.biometricTargetVault) {
                    "decoy" -> onRequestDecoyBiometricUnlock
                    else -> onRequestBiometricUnlock
                }
                val secondaryUnlock = when (state.biometricTargetVault) {
                    "decoy" -> if (state.biometricEnabled) onRequestBiometricUnlock else null
                    else -> if (state.decoyBiometricEnabled) onRequestDecoyBiometricUnlock else null
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            enabled = !state.isBusy,
                            onClick = primaryUnlock,
                            onLongClick = secondaryUnlock
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tx("UNLOCK WITH FINGERPRINT"),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

internal enum class SortMode {
    RECENTLY_EDITED,
    EXPIRING_SOON,
    REMINDER_DUE,
    PINNED_FIRST
}

internal enum class CreatedRangeFilter {
    ANY,
    LAST_24H,
    LAST_7D,
    LAST_30D
}

internal const val DAY_MS = 24L * 60L * 60L * 1000L

internal fun dayStart(epochMillis: Long): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = epochMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

internal fun computeBestCreationStreak(notes: List<Note>): Int {
    val days = notes.map { dayStart(it.createdAt) }.distinct().sorted()
    if (days.isEmpty()) return 0
    var best = 1
    var current = 1
    for (i in 1 until days.size) {
        if (days[i] - days[i - 1] == DAY_MS) {
            current += 1
        } else {
            current = 1
        }
        if (current > best) best = current
    }
    return best
}

internal fun computeCurrentCreationStreak(notes: List<Note>, nowMillis: Long): Int {
    val daySet = notes.map { dayStart(it.createdAt) }.toSet()
    if (daySet.isEmpty()) return 0
    var cursor = dayStart(nowMillis)
    if (!daySet.contains(cursor) && daySet.contains(cursor - DAY_MS)) {
        cursor -= DAY_MS
    }
    var streak = 0
    while (daySet.contains(cursor)) {
        streak += 1
        cursor -= DAY_MS
    }
    return streak
}


package com.androidircx.nulvex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidircx.nulvex.i18n.tx
import com.androidircx.nulvex.ui.theme.BuiltInThemes
import com.androidircx.nulvex.ui.theme.ThemeColors
import com.androidircx.nulvex.ui.theme.ThemeMode
import com.androidircx.nulvex.ui.theme.ThemePalette

@Composable
internal fun ThemePaletteSection(
    state: UiState,
    onUpdateThemePalette: (String) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onSaveCustomTheme: (ThemePalette) -> Unit,
    onDeleteCustomTheme: (String) -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val darkNow = when (state.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    var editorTarget by remember { mutableStateOf<ThemePalette?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Text(tx("Color theme"), style = MaterialTheme.typography.labelLarge, color = onSurface)
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        state.availableThemes.forEach { theme ->
            ThemeSwatch(
                theme = theme,
                dark = darkNow,
                selected = theme.id == state.themePaletteId,
                onClick = { onUpdateThemePalette(theme.id) },
                onEdit = if (!theme.builtIn) {
                    { editorTarget = theme; showEditor = true }
                } else {
                    null
                }
            )
        }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(tx("Material You"), style = MaterialTheme.typography.bodyMedium, color = onSurface)
            Text(
                tx("Use system wallpaper colors (Android 12+)"),
                style = MaterialTheme.typography.labelSmall,
                color = onSurface.copy(alpha = 0.6f)
            )
        }
        Switch(checked = state.dynamicColor, onCheckedChange = onToggleDynamicColor)
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedButton(onClick = { editorTarget = null; showEditor = true }) {
        Text(tx("Create custom theme"))
    }

    if (showEditor) {
        CustomThemeEditorDialog(
            existing = editorTarget,
            onDismiss = { showEditor = false },
            onSave = { palette ->
                onSaveCustomTheme(palette)
                showEditor = false
            },
            onDelete = editorTarget?.let { target ->
                {
                    onDeleteCustomTheme(target.id)
                    showEditor = false
                }
            }
        )
    }
}

@Composable
internal fun ThemeSwatch(
    theme: ThemePalette,
    dark: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    onEdit: (() -> Unit)?
) {
    val colors = theme.colorsFor(dark)
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(colors.background))
                .border(
                    width = if (selected) 3.dp else 1.dp,
                    color = if (selected) primary else onSurface.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(colors.primary))
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(theme.name, style = MaterialTheme.typography.labelSmall, color = onSurface)
        if (onEdit != null) {
            Text(
                tx("Edit"),
                style = MaterialTheme.typography.labelSmall,
                color = primary,
                modifier = Modifier.clickable(onClick = onEdit)
            )
        }
    }
}

@Composable
internal fun CustomThemeEditorDialog(
    existing: ThemePalette?,
    onDismiss: () -> Unit,
    onSave: (ThemePalette) -> Unit,
    onDelete: (() -> Unit)?
) {
    val seed = existing?.dark ?: BuiltInThemes.DEFAULT.dark
    var name by remember { mutableStateOf(existing?.name ?: "My theme") }
    var primary by remember { mutableStateOf(hexOf(seed.primary)) }
    var secondary by remember { mutableStateOf(hexOf(seed.secondary)) }
    var tertiary by remember { mutableStateOf(hexOf(seed.tertiary)) }
    var background by remember { mutableStateOf(hexOf(seed.background)) }
    var surface by remember { mutableStateOf(hexOf(seed.surface)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) tx("Create theme") else tx("Edit theme")) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(tx("Name")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                HexColorField(tx("Primary"), primary) { primary = it }
                HexColorField(tx("Secondary"), secondary) { secondary = it }
                HexColorField(tx("Accent"), tertiary) { tertiary = it }
                HexColorField(tx("Background"), background) { background = it }
                HexColorField(tx("Surface"), surface) { surface = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val colors = ThemeColors(
                    primary = parseHexColor(primary),
                    secondary = parseHexColor(secondary),
                    tertiary = parseHexColor(tertiary),
                    background = parseHexColor(background),
                    surface = parseHexColor(surface)
                )
                val id = existing?.id ?: "custom_${System.currentTimeMillis()}"
                onSave(ThemePalette(id, name.ifBlank { "Custom" }, false, colors, colors))
            }) { Text(tx("Save")) }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text(tx("Delete")) }
                }
                TextButton(onClick = onDismiss) { Text(tx("Cancel")) }
            }
        }
    )
}

@Composable
internal fun HexColorField(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(parseHexColor(value)))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(6.dp))
}

internal fun hexOf(c: Long): String = "#%06X".format(c and 0xFFFFFFL)

internal fun parseHexColor(s: String): Long {
    val h = s.trim().removePrefix("#")
    val v = h.toLongOrNull(16) ?: return 0xFF000000L
    return if (h.length <= 6) 0xFF000000L or v else v
}

@Composable
internal fun MarkupChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

internal fun Color.toCssHex(): String =
    "#%02X%02X%02X".format((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())

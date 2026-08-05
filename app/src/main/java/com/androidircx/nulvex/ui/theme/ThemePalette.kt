package com.androidircx.nulvex.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/**
 * Five seed colors that define a theme for one appearance (light or dark). The rest of the
 * Material 3 [ColorScheme] (on-colors, containers, outlines) is derived, so a custom theme
 * only needs the user to choose these five. Stored as ARGB longs (0xAARRGGBB) for easy JSON
 * persistence.
 */
data class ThemeColors(
    val primary: Long,
    val secondary: Long,
    val tertiary: Long,
    val background: Long,
    val surface: Long
)

/** A named theme with a light and dark variant. Built-in themes ship with the app; custom
 *  themes are created/edited by the user and persisted. */
data class ThemePalette(
    val id: String,
    val name: String,
    val builtIn: Boolean,
    val dark: ThemeColors,
    val light: ThemeColors
) {
    fun colorsFor(dark: Boolean): ThemeColors = if (dark) this.dark else this.light
}

private fun contrastOn(c: Color): Color =
    if (c.luminance() > 0.45f) Color(0xFF10130F) else Color(0xFFF3F7F2)

private fun blend(a: Color, b: Color, t: Float): Color = Color(
    red = a.red + (b.red - a.red) * t,
    green = a.green + (b.green - a.green) * t,
    blue = a.blue + (b.blue - a.blue) * t,
    alpha = 1f
)

/** Derives a full Material 3 [ColorScheme] from the five seed colors. */
fun ThemeColors.toColorScheme(dark: Boolean): ColorScheme {
    val primary = Color(primary)
    val secondary = Color(secondary)
    val tertiary = Color(tertiary)
    val background = Color(background)
    val surface = Color(surface)
    val base = if (dark) darkColorScheme() else lightColorScheme()
    val onSurface = contrastOn(surface)
    val primaryContainer = blend(primary, surface, 0.7f)
    val secondaryContainer = blend(secondary, surface, 0.7f)
    val tertiaryContainer = blend(tertiary, surface, 0.7f)
    val surfaceVariant = blend(surface, if (dark) Color.White else Color.Black, 0.06f)
    return base.copy(
        primary = primary,
        onPrimary = contrastOn(primary),
        primaryContainer = primaryContainer,
        onPrimaryContainer = contrastOn(primaryContainer),
        secondary = secondary,
        onSecondary = contrastOn(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contrastOn(secondaryContainer),
        tertiary = tertiary,
        onTertiary = contrastOn(tertiary),
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = contrastOn(tertiaryContainer),
        background = background,
        onBackground = contrastOn(background),
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurface.copy(alpha = 0.75f),
        outline = onSurface.copy(alpha = 0.4f),
        outlineVariant = onSurface.copy(alpha = 0.2f),
        error = Color(0xFFB0543A),
        onError = Color(0xFFF3F7F2)
    )
}

package com.androidircx.nulvex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Brass,
    secondary = Moss,
    tertiary = Ember,
    background = Ink,
    surface = Coal,
    onPrimary = Ink,
    onSecondary = Ice,
    onTertiary = Ice,
    onBackground = Ice,
    onSurface = Ice
)

private val LightColorScheme = lightColorScheme(
    primary = Moss,
    secondary = Brass,
    tertiary = Ember,
    background = Sand,
    surface = Ice,
    onPrimary = Ink,
    onSecondary = Ink,
    onTertiary = Ink,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun NULVEXTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

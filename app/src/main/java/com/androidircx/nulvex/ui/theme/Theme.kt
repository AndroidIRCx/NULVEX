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
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

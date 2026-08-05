package com.androidircx.nulvex.ui.theme

/**
 * Curated built-in themes. "vault" preserves the app's original look and is the default.
 * Each theme provides a hand-tuned light and dark seed set; the full Material scheme is
 * derived in [toColorScheme].
 */
object BuiltInThemes {

    val VAULT = ThemePalette(
        id = "vault",
        name = "Vault",
        builtIn = true,
        dark = ThemeColors(
            primary = 0xFFC7A76E,   // Brass
            secondary = 0xFF3E5B4B, // Moss
            tertiary = 0xFFB0543A,  // Ember
            background = 0xFF0C1110, // Ink
            surface = 0xFF141B1A     // Coal
        ),
        light = ThemeColors(
            primary = 0xFF3E5B4B,   // Moss
            secondary = 0xFFC7A76E, // Brass
            tertiary = 0xFFB0543A,  // Ember
            background = 0xFFE7DCC7, // Sand
            surface = 0xFFF3EFE4
        )
    )

    val MIDNIGHT = ThemePalette(
        id = "midnight",
        name = "Midnight",
        builtIn = true,
        dark = ThemeColors(
            primary = 0xFF7AA2F7,
            secondary = 0xFF6C7086,
            tertiary = 0xFFBB9AF7,
            background = 0xFF0B0F1A,
            surface = 0xFF141A2A
        ),
        light = ThemeColors(
            primary = 0xFF3A5BB8,
            secondary = 0xFF5B6480,
            tertiary = 0xFF7C4DDC,
            background = 0xFFEDF1FA,
            surface = 0xFFFAFBFF
        )
    )

    val SLATE = ThemePalette(
        id = "slate",
        name = "Slate",
        builtIn = true,
        dark = ThemeColors(
            primary = 0xFFB8C0CC,
            secondary = 0xFF8A93A0,
            tertiary = 0xFF9AB0A6,
            background = 0xFF101214,
            surface = 0xFF1A1D21
        ),
        light = ThemeColors(
            primary = 0xFF44515F,
            secondary = 0xFF6B7683,
            tertiary = 0xFF4E7A67,
            background = 0xFFEDEFF2,
            surface = 0xFFFBFCFD
        )
    )

    val EMERALD = ThemePalette(
        id = "emerald",
        name = "Emerald",
        builtIn = true,
        dark = ThemeColors(
            primary = 0xFF4BD4A0,
            secondary = 0xFF3E7C63,
            tertiary = 0xFFE0B15A,
            background = 0xFF07130F,
            surface = 0xFF0F1D18
        ),
        light = ThemeColors(
            primary = 0xFF11855F,
            secondary = 0xFF3E7C63,
            tertiary = 0xFFB07B12,
            background = 0xFFE6F2EC,
            surface = 0xFFF6FBF8
        )
    )

    val ROSE = ThemePalette(
        id = "rose",
        name = "Rose",
        builtIn = true,
        dark = ThemeColors(
            primary = 0xFFF7869B,
            secondary = 0xFF9A6B8C,
            tertiary = 0xFFE0A15A,
            background = 0xFF150B0F,
            surface = 0xFF20141A
        ),
        light = ThemeColors(
            primary = 0xFFC03A62,
            secondary = 0xFF8A5A78,
            tertiary = 0xFFB07B12,
            background = 0xFFF9EAEF,
            surface = 0xFFFEF6F9
        )
    )

    val AMBER = ThemePalette(
        id = "amber",
        name = "Amber",
        builtIn = true,
        dark = ThemeColors(
            primary = 0xFFE0A94B,
            secondary = 0xFF8A6E3E,
            tertiary = 0xFF7AA98A,
            background = 0xFF13100A,
            surface = 0xFF1E1912
        ),
        light = ThemeColors(
            primary = 0xFFB07914,
            secondary = 0xFF8A6E3E,
            tertiary = 0xFF3E7C63,
            background = 0xFFF6EFE0,
            surface = 0xFFFDF9F1
        )
    )

    val ALL: List<ThemePalette> = listOf(VAULT, MIDNIGHT, SLATE, EMERALD, ROSE, AMBER)

    val DEFAULT: ThemePalette = VAULT

    fun byId(id: String?): ThemePalette? = ALL.firstOrNull { it.id == id }
}

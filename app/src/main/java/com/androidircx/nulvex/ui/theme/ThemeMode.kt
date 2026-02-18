package com.androidircx.nulvex.ui.theme

enum class ThemeMode(val id: String) {
    SYSTEM("system"),
    DARK("dark"),
    LIGHT("light");

    companion object {
        fun fromId(value: String?): ThemeMode {
            return entries.firstOrNull { it.id == value } ?: SYSTEM
        }
    }
}

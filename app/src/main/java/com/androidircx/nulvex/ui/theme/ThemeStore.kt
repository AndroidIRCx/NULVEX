package com.androidircx.nulvex.ui.theme

import com.androidircx.nulvex.security.AppPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Resolves and persists themes. Built-in themes come from [BuiltInThemes]; user-created
 * custom themes are stored as JSON in [AppPreferences] (which panic-wipe already clears).
 */
class ThemeStore(private val appPreferences: AppPreferences) {

    fun selectedId(): String = appPreferences.getThemePaletteId()

    fun setSelectedId(id: String) = appPreferences.setThemePaletteId(id)

    fun dynamicColorEnabled(): Boolean = appPreferences.isDynamicColor()

    fun setDynamicColorEnabled(value: Boolean) = appPreferences.setDynamicColor(value)

    fun customThemes(): List<ThemePalette> = decode(appPreferences.getCustomThemesJson())

    /** Built-in themes followed by the user's custom ones. */
    fun availableThemes(): List<ThemePalette> = BuiltInThemes.ALL + customThemes()

    fun resolve(id: String): ThemePalette =
        availableThemes().firstOrNull { it.id == id } ?: BuiltInThemes.DEFAULT

    /** Insert or replace a custom theme (matched by id). Returns the updated custom list. */
    fun saveCustom(palette: ThemePalette): List<ThemePalette> {
        val custom = palette.copy(builtIn = false)
        val updated = customThemes().filterNot { it.id == custom.id } + custom
        appPreferences.setCustomThemesJson(encode(updated))
        return updated
    }

    fun deleteCustom(id: String): List<ThemePalette> {
        val updated = customThemes().filterNot { it.id == id }
        appPreferences.setCustomThemesJson(encode(updated))
        if (selectedId() == id) setSelectedId(BuiltInThemes.DEFAULT.id)
        return updated
    }

    private fun encode(themes: List<ThemePalette>): String {
        val arr = JSONArray()
        themes.forEach { theme ->
            arr.put(
                JSONObject().apply {
                    put("id", theme.id)
                    put("name", theme.name)
                    put("dark", encodeColors(theme.dark))
                    put("light", encodeColors(theme.light))
                }
            )
        }
        return arr.toString()
    }

    private fun encodeColors(c: ThemeColors): JSONObject = JSONObject().apply {
        put("primary", c.primary)
        put("secondary", c.secondary)
        put("tertiary", c.tertiary)
        put("background", c.background)
        put("surface", c.surface)
    }

    private fun decode(json: String): List<ThemePalette> {
        return runCatching {
            val arr = JSONArray(json)
            (0 until arr.length()).mapNotNull { i ->
                val obj = arr.optJSONObject(i) ?: return@mapNotNull null
                val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return@mapNotNull null
                ThemePalette(
                    id = id,
                    name = obj.optString("name", "Custom"),
                    builtIn = false,
                    dark = decodeColors(obj.optJSONObject("dark")),
                    light = decodeColors(obj.optJSONObject("light"))
                )
            }
        }.getOrElse { emptyList() }
    }

    private fun decodeColors(obj: JSONObject?): ThemeColors {
        val fallback = BuiltInThemes.DEFAULT.dark
        if (obj == null) return fallback
        return ThemeColors(
            primary = obj.optLong("primary", fallback.primary),
            secondary = obj.optLong("secondary", fallback.secondary),
            tertiary = obj.optLong("tertiary", fallback.tertiary),
            background = obj.optLong("background", fallback.background),
            surface = obj.optLong("surface", fallback.surface)
        )
    }
}

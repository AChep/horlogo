package com.artemchep.horlogo

import android.content.Context
import android.content.SharedPreferences
import com.artemchep.config.ConfigBase
import com.artemchep.horlogo.ui.PALETTE_BLUE

/**
 * @author Artem Chepurnoy
 */
object Config : ConfigBase() {

    const val KEY_ACCENT_COLOR = "accent"
    // Layout
    const val KEY_LAYOUT = "layout"
    const val LAYOUT_HORIZONTAL = "layout::horizontal"
    const val LAYOUT_VERTICAL = "layout::vertical"
    // Theme
    const val KEY_THEME = "theme"
    const val THEME_BLACK = "BLACK"
    const val THEME_DARK = "DARK"
    const val THEME_LIGHT = "LIGHT"

    var accentColor: Int by configProperty(KEY_ACCENT_COLOR, PALETTE_BLUE)
    var themeName: String by configProperty(KEY_THEME, THEME_BLACK)
    var layoutName: String by configProperty(KEY_LAYOUT, LAYOUT_VERTICAL)

    override fun onInitVars(map: Map<String, *>) {
        accentColor = map.getOrDefaultTs(KEY_ACCENT_COLOR, accentColor)
        layoutName = map.getOrDefaultTs(KEY_LAYOUT, layoutName)
        themeName = map.getOrDefaultTs(KEY_THEME, themeName)
    }

    override fun getConfigSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("config", 0)
    }

}
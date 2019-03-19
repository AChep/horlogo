package com.artemchep.horlogo

import com.artemchep.config.common.SharedPrefConfig
import com.artemchep.horlogo.ui.PALETTE_BLUE

/**
 * @author Artem Chepurnoy
 */
object Cfg : SharedPrefConfig("config") {

    const val KEY_ACCENT_COLOR = "accent"
    const val KEY_GRAYSCALE_IN_AMBIENT = "grayscale_in_ambient"
    // Layout
    const val KEY_LAYOUT = "layout"
    const val LAYOUT_HORIZONTAL = "layout::horizontal"
    const val LAYOUT_VERTICAL = "layout::vertical"
    // Theme
    const val KEY_THEME = "theme"
    const val THEME_BLACK = "BLACK"
    const val THEME_DARK = "DARK"
    const val THEME_LIGHT = "LIGHT"

    var accentColor: Int by configDelegate(KEY_ACCENT_COLOR, PALETTE_BLUE)
    var themeName: String by configDelegate(KEY_THEME, THEME_BLACK)
    var layoutName: String by configDelegate(KEY_LAYOUT, LAYOUT_VERTICAL)
    var grayscaleInAmbient: Boolean by configDelegate(KEY_GRAYSCALE_IN_AMBIENT, true)

}
package com.artemchep.horlogo.presenters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.artemchep.config.Config
import com.artemchep.horlogo.Cfg
import com.artemchep.horlogo.contracts.IMainPresenter
import com.artemchep.horlogo.contracts.IMainView
import com.artemchep.horlogo.ui.*
import com.artemchep.horlogo.ui.model.ConfigPickerItem
import com.artemchep.horlogo.ui.model.Theme
import com.artemchep.horlogo_common.R


/**
 * @author Artem Chepurnoy
 */
abstract class MainPresenter(private val context: Context) : IMainPresenter {

    companion object {
        private const val REQUEST_CODE_ACCENT_COLOR = 111
        private const val REQUEST_CODE_LAYOUT = 222
        private const val REQUEST_CODE_THEME = 333
        private const val REQUEST_CODE_GRAYSCALE = 444
    }

    override var view: IMainView? = null

    private val configListener = object : Config.OnConfigChangedListener<String> {

        override fun onConfigChanged(keys: Set<String>) {
            keys.forEach { key ->
                when (key) {
                    Cfg.KEY_LAYOUT -> updateLayoutItem()
                    Cfg.KEY_THEME -> updateThemeItem()
                    Cfg.KEY_ACCENT_COLOR -> updateAccentItem()
                    Cfg.KEY_GRAYSCALE_IN_AMBIENT -> updateGrayscaleItem()
                }
            }
        }

    }

    //
    // Map
    //

    /**
     * Map of the palette and its
     * names.
     */
    private val paletteMap = mapOf(
        PALETTE_RED to getString(R.string.red),
        PALETTE_PINK to getString(R.string.pink),
        PALETTE_PURPLE to getString(R.string.purple),
        PALETTE_DEEP_PURPLE to getString(R.string.deep_purple),
        PALETTE_INDIGO to getString(R.string.indigo),
        PALETTE_BLUE to getString(R.string.blue),
        PALETTE_CYAN to getString(R.string.cyan),
        PALETTE_TEAL to getString(R.string.teal),
        PALETTE_GREEN to getString(R.string.green),
        PALETTE_LIGHT_GREEN to getString(R.string.light_green),
        PALETTE_LIME to getString(R.string.lime),
        PALETTE_YELLOW to getString(R.string.yellow),
        PALETTE_AMBER to getString(R.string.amber),
        PALETTE_ORANGE to getString(R.string.orange),
        PALETTE_DEEP_ORANGE to getString(R.string.deep_orange),
        PALETTE_BROWN to getString(R.string.brown),
        PALETTE_GREY to getString(R.string.grey),
        PALETTE_WHITE to getString(R.string.white)
    )

    /**
     * Map of the themes and its
     * names
     */
    private val themeMap = mapOf(
        Cfg.THEME_BLACK to getString(R.string.theme_black),
        Cfg.THEME_DARK to getString(R.string.theme_dark),
        Cfg.THEME_LIGHT to getString(R.string.theme_light)
    )

    /**
     * Map of the layouts and its
     * names
     */
    private val layoutMap = mapOf(
        Cfg.LAYOUT_VERTICAL to getString(R.string.layout_vertical),
        Cfg.LAYOUT_HORIZONTAL to getString(R.string.layout_horizontal)
    )

    /**
     * Map of the grayscale and its
     * names
     */
    private val grayscaleMap = mapOf(
        true to getString(R.string.grayscale_on),
        false to getString(R.string.grayscale_off)
    )

    //
    // Items
    //

    override fun onResume() {
        super.onResume()

        // Listen to the config changes and update the
        // user interface.
        Cfg.observe(configListener)

        // Update the interface, cause the config could change
        // while we were paused.
        updateItems()
    }

    private fun updateItems() {
        updateLayoutItem(false)
        updateThemeItem(false)
        updateAccentItem(false)
        updateGrayscaleItem(false)
        view!!.notifyDataChanged()
    }

    private fun updateLayoutItem(notifyItemChanged: Boolean = true) {
        val layoutName = layoutMap[Cfg.layoutName]
        updateSingleItem(IMainPresenter.ITEM_LAYOUT, layoutName, notifyItemChanged)
    }

    private fun updateThemeItem(notifyItemChanged: Boolean = true) {
        val themeName = themeMap[Cfg.themeName]
        updateSingleItem(IMainPresenter.ITEM_THEME, themeName, notifyItemChanged)
    }

    private fun updateAccentItem(notifyItemChanged: Boolean = true) {
        val accentColorName = paletteMap[Cfg.accentColor]
        updateSingleItem(IMainPresenter.ITEM_ACCENT_COLOR, accentColorName, notifyItemChanged)
    }

    private fun updateGrayscaleItem(notifyItemChanged: Boolean = true) {
        val grayscaleName = grayscaleMap[Cfg.grayscaleInAmbient]
        updateSingleItem(IMainPresenter.ITEM_GRAYSCALE, grayscaleName, notifyItemChanged)
    }

    private fun updateSingleItem(id: Int, summary: String?, notifyItemChanged: Boolean) {
        items
            .indexOfFirst { it.id == id }
            .also { position ->
                items[position].summary = summary

                if (notifyItemChanged) {
                    // Tell view to update the item at
                    // position
                    view?.notifyItemChanged(position)
                }
            }
    }

    override fun onPause() {
        // Stop listening to the config
        // changes
        Cfg.removeObserver(configListener)
        super.onPause()
    }

    override fun result(requestCode: Int, resultCode: Int, key: String?) {
        if (resultCode != Activity.RESULT_OK || key == null) {
            return
        }

        Cfg.edit(context) {
            when (requestCode) {
                REQUEST_CODE_LAYOUT -> Cfg.layoutName = key
                REQUEST_CODE_THEME -> Cfg.themeName = key
                REQUEST_CODE_ACCENT_COLOR -> {
                    val color = key.toInt()
                    Cfg.accentColor = color
                }
                REQUEST_CODE_GRAYSCALE -> {
                    val grayscale = key.toBoolean()
                    Cfg.grayscaleInAmbient = grayscale
                }
            }
        }
    }

    override fun navigateTo(itemId: Int) {
        when (itemId) {
            IMainPresenter.ITEM_COMPLICATIONS -> view!!.showComplicationsScreen()
            IMainPresenter.ITEM_LAYOUT -> {
                // Show the picker for layout and
                // wait for the result.
                val title = getString(R.string.config_layout)
                val items = layoutMap.map { (key, name) -> ConfigPickerItem(key, 0, name) }
                view!!.showPickerScreenForResult(title, Cfg.layoutName, items, REQUEST_CODE_LAYOUT)
            }
            IMainPresenter.ITEM_THEME -> {
                // Show the picker for theme and
                // wait for the result.
                val title = getString(R.string.config_theme)
                val items = themeMap.map { (key, name) ->
                    val color = when (key) {
                        Cfg.THEME_BLACK -> Theme.BLACK
                        Cfg.THEME_DARK -> Theme.DARK
                        else -> Theme.LIGHT
                    }.backgroundColor

                    // Convert to the picker item
                    ConfigPickerItem(key, color, name)
                }
                view!!.showPickerScreenForResult(title, Cfg.themeName, items, REQUEST_CODE_THEME)
            }
            IMainPresenter.ITEM_ACCENT_COLOR -> {
                // Show the picker for accent and
                // wait for the result.
                val title = getString(R.string.config_accent)
                val items = paletteMap.map { (color, name) ->
                    ConfigPickerItem(
                        color.toString(),
                        color,
                        name
                    )
                }
                val current = Cfg.accentColor.toString()
                view!!.showPickerScreenForResult(title, current, items, REQUEST_CODE_ACCENT_COLOR)
            }
            IMainPresenter.ITEM_GRAYSCALE -> {
                // Show the picker for grayscale in an ambient mode and
                // wait for the result.
                val title = getString(R.string.config_grayscale)
                val items = grayscaleMap.map { (key, name) ->
                    ConfigPickerItem(key.toString(), 0, name)
                }
                val current = Cfg.grayscaleInAmbient.toString()
                view!!.showPickerScreenForResult(title, current, items, REQUEST_CODE_GRAYSCALE)
            }
            IMainPresenter.ITEM_ABOUT -> view!!.showAboutScreen()
        }
    }

    protected fun getString(@StringRes stringRes: Int): String = context.getString(stringRes)

    protected fun getDrawable(@DrawableRes drawableRes: Int): Drawable =
        context.getDrawable(drawableRes)!!

}

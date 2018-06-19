package com.artemchep.horlogo.presenters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.artemchep.config.ConfigBase
import com.artemchep.horlogo.Config
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IMainPresenter
import com.artemchep.horlogo.contracts.IMainView
import com.artemchep.horlogo.extensions.sealed
import com.artemchep.horlogo.ui.*
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.model.ConfigPickerItem
import com.artemchep.horlogo.ui.model.Theme


/**
 * @author Artem Chepurnoy
 */
class MainPresenter(private val context: Context) : IMainPresenter {

    companion object {
        const val ITEM_COMPLICATIONS = 1
        const val ITEM_LAYOUT = 2
        const val ITEM_THEME = 3
        const val ITEM_ACCENT_COLOR = 4
        const val ITEM_ABOUT = 5

        private const val REQUEST_CODE_ACCENT_COLOR = 111
        private const val REQUEST_CODE_LAYOUT = 222
        private const val REQUEST_CODE_THEME = 333
    }

    override var view: IMainView? = null
        set(value) {
            field = value
            items = value?.items?.apply {
                // Inject the models of the
                // list.
                clear()
                addAll(itemsSrc)
            }
        }

    private var items: MutableList<ConfigItem>? = null

    private val configListener = object : ConfigBase.OnConfigChangedListener {

        override fun onConfigChanged(key: String) {
            when (key) {
                Config.KEY_LAYOUT -> updateLayoutItem()
                Config.KEY_THEME -> updateThemeItem()
                Config.KEY_ACCENT_COLOR -> updateAccentItem()
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
            Config.THEME_BLACK to getString(R.string.theme_black),
            Config.THEME_DARK to getString(R.string.theme_dark),
            Config.THEME_LIGHT to getString(R.string.theme_light)
    )

    /**
     * Map of the layouts and its
     * names
     */
    private val layoutMap = mapOf(
            Config.LAYOUT_VERTICAL to getString(R.string.layout_vertical),
            Config.LAYOUT_HORIZONTAL to getString(R.string.layout_horizontal)
    )

    //
    // Items
    //

    private val itemsSrc = listOf(
            ConfigItem(
                    id = ITEM_COMPLICATIONS,
                    icon = getDrawable(R.drawable.ic_view),
                    title = getString(R.string.config_complications)),
            ConfigItem(
                    id = ITEM_LAYOUT,
                    icon = getDrawable(R.drawable.ic_view_carousel),
                    title = getString(R.string.config_layout)),
            ConfigItem(
                    id = ITEM_THEME,
                    icon = getDrawable(R.drawable.ic_invert_colors),
                    title = getString(R.string.config_theme)),
            ConfigItem(
                    id = ITEM_ACCENT_COLOR,
                    icon = getDrawable(R.drawable.ic_palette),
                    title = getString(R.string.config_accent)),
            ConfigItem(
                    id = ITEM_ABOUT,
                    icon = getDrawable(R.drawable.ic_information_outline),
                    title = getString(R.string.config_about))
    )

    override fun onResume() {
        super.onResume()

        // Listen to the config changes and update the
        // user interface.
        Config.addListener(configListener)

        // Update the interface, cause the config could change
        // while we were paused.
        updateItems()
    }

    private fun updateItems() {
        updateLayoutItem(false)
        updateThemeItem(false)
        updateAccentItem(false)
        view!!.notifyDataChanged()
    }

    private fun updateLayoutItem(notifyItemChanged: Boolean = true) {
        val layoutName = layoutMap[Config.layoutName]
        updateSingleItem(ITEM_LAYOUT, layoutName, notifyItemChanged)
    }

    private fun updateThemeItem(notifyItemChanged: Boolean = true) {
        val themeName = themeMap[Config.themeName]
        updateSingleItem(ITEM_THEME, themeName, notifyItemChanged)
    }

    private fun updateAccentItem(notifyItemChanged: Boolean = true) {
        val accentColorName = paletteMap[Config.accentColor]
        updateSingleItem(ITEM_ACCENT_COLOR, accentColorName, notifyItemChanged)
    }

    private fun updateSingleItem(id: Int, summary: String?, notifyItemChanged: Boolean) {
        items!!
                .indexOfFirst { it.id == id }
                .also { position ->
                    items!![position].summary = summary

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
        Config.removeListener(configListener)
        super.onPause()
    }

    override fun result(requestCode: Int, resultCode: Int, key: String?) {
        if (resultCode != Activity.RESULT_OK || key == null) {
            return
        }

        Config.edit(context) {
            when (requestCode) {
                REQUEST_CODE_LAYOUT -> Config.layoutName = key
                REQUEST_CODE_THEME -> Config.themeName = key
                REQUEST_CODE_ACCENT_COLOR -> {
                    val color = key.toInt()
                    Config.accentColor = color
                }
            }
        }
    }

    override fun navigateTo(dst: IMainPresenter.Destination) {
        when (dst) {
            IMainPresenter.Destination.COMPLICATION -> view!!.showComplicationsScreen()
            IMainPresenter.Destination.LAYOUT -> {
                // Show the picker for layout and
                // wait for the result.
                val title = getString(R.string.config_layout)
                val items = layoutMap.map { (key, name) -> ConfigPickerItem(key, 0, name) }
                view!!.showPickerScreenForResult(title, Config.layoutName, items, REQUEST_CODE_LAYOUT)
            }
            IMainPresenter.Destination.THEME -> {
                // Show the picker for theme and
                // wait for the result.
                val title = getString(R.string.config_theme)
                val items = themeMap.map { (key, name) ->
                    val color = when (key) {
                        Config.THEME_BLACK -> Theme.BLACK
                        Config.THEME_DARK -> Theme.DARK
                        else -> Theme.LIGHT
                    }.backgroundColor

                    // Convert to the picker item
                    ConfigPickerItem(key, color, name)
                }
                view!!.showPickerScreenForResult(title, Config.themeName, items, REQUEST_CODE_THEME)
            }
            IMainPresenter.Destination.ACCENT -> {
                // Show the picker for accent and
                // wait for the result.
                val title = getString(R.string.config_accent)
                val items = paletteMap.map { (color, name) -> ConfigPickerItem(color.toString(), color, name) }
                val current = Config.accentColor.toString()
                view!!.showPickerScreenForResult(title, current, items, REQUEST_CODE_ACCENT_COLOR)
            }
            IMainPresenter.Destination.ABOUT -> view!!.showAboutScreen()
        }.sealed()
    }

    private fun getString(@StringRes stringRes: Int): String = context.getString(stringRes)

    private fun getDrawable(@DrawableRes drawableRes: Int): Drawable = context.getDrawable(drawableRes)

}

package com.artemchep.horlogo.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.SparseArray
import android.view.View
import com.artemchep.horlogo.Config
import com.artemchep.horlogo.R
import com.artemchep.horlogo.ui.Palette
import com.artemchep.horlogo.ui.Theme
import com.artemchep.horlogo.ui.adapter.ConfigAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.model.ConfigPickerItem
import com.artemchep.horlogo.util.ConfigManager
import kotlinx.android.synthetic.main.activity_config.*


/**
 * @author Artem Chepurnoy
 */
class ConfigActivity : Activity(), OnItemClickListener<ConfigItem> {

    companion object {
        private const val ITEM_COMPLICATIONS = 1
        private const val ITEM_THEME = 4
        private const val ITEM_ACCENT_COLOR = 2
        private const val ITEM_ABOUT = 3

        private const val REQUEST_CODE_ACCENT_COLOR = 101
        private const val REQUEST_CODE_THEME = 102
    }

    private val configManager by lazy { ConfigManager(this) }

    private lateinit var items: MutableList<ConfigItem>

    private lateinit var adapter: ConfigAdapter

    private lateinit var themeMap: Map<String, String>

    private lateinit var colorSparse: SparseArray<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items = mutableListOf(
                ConfigItem(
                        id = ITEM_COMPLICATIONS,
                        icon = getDrawable(R.drawable.ic_view),
                        title = getString(R.string.config_complications)),
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

        themeMap = hashMapOf(
                Theme.BLACK.name to getString(R.string.theme_black),
                Theme.DARK.name to getString(R.string.theme_dark),
                Theme.LIGHT.name to getString(R.string.theme_light)
        )

        colorSparse = SparseArray<String>().apply {
            put(Palette.RED, getString(R.string.red))
            put(Palette.PINK, getString(R.string.pink))
            put(Palette.PURPLE, getString(R.string.purple))
            put(Palette.DEEP_PURPLE, getString(R.string.deep_purple))
            put(Palette.INDIGO, getString(R.string.indigo))
            put(Palette.BLUE, getString(R.string.blue))
            put(Palette.CYAN, getString(R.string.cyan))
            put(Palette.TEAL, getString(R.string.teal))
            put(Palette.GREEN, getString(R.string.green))
            put(Palette.LIGHT_GREEN, getString(R.string.light_green))
            put(Palette.LIME, getString(R.string.lime))
            put(Palette.YELLOW, getString(R.string.yellow))
            put(Palette.AMBER, getString(R.string.amber))
            put(Palette.ORANGE, getString(R.string.orange))
            put(Palette.DEEP_ORANGE, getString(R.string.deep_orange))
            put(Palette.BROWN, getString(R.string.brown))
            put(Palette.GREY, getString(R.string.grey))
            put(Color.WHITE, getString(R.string.white))
        }

        val title = getString(R.string.config)
        adapter = ConfigAdapter(items, title).apply {
            onItemClickListener = this@ConfigActivity
        }

        setContentView(R.layout.activity_config)

        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ConfigActivity)

            adapter = this@ConfigActivity.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        configManager.start {
            updateThemeItem()
            updateAccentColorItem()
        }

        updateThemeItem()
        updateAccentColorItem()
    }

    override fun onStop() {
        configManager.stop()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            REQUEST_CODE_ACCENT_COLOR -> {
                val color = data.getStringExtra(ConfigPickerActivity.RESULT_KEY)

                // Set current accent color and say that the
                // config has changed.
                Config.edit(this) {
                    accentColor = color.toIntOrNull() ?: accentColor
                }.also {
                    ConfigManager.broadcastConfigChanged(this)
                }
            }
            REQUEST_CODE_THEME -> {
                val theme = data.getStringExtra(ConfigPickerActivity.RESULT_KEY)

                // Set current theme id and say that the
                // config has changed.
                Config.edit(this) {
                    themeName = theme ?: Theme.BLACK.name
                }.also {
                    ConfigManager.broadcastConfigChanged(this)
                }
            }
        }
    }

    override fun onItemClick(view: View, model: ConfigItem) {
        when (model.id) {
            ITEM_COMPLICATIONS -> {
                val intent = Intent(this, ConfigComplicationsActivity::class.java)
                startActivity(intent)
            }
            ITEM_THEME -> {
                val intent = ConfigPickerActivity.newIntent(this,
                        Config.themeName,
                        getString(R.string.config_theme),
                        Theme.values()
                                .map { ConfigPickerItem(it.name, it.backgroundColor, themeMap[it.name]!!) }
                                .let {
                                    ArrayList(it)
                                }
                )
                startActivityForResult(intent, REQUEST_CODE_THEME)
            }
            ITEM_ACCENT_COLOR -> {
                val intent = ConfigPickerActivity.newIntent(this,
                        Config.accentColor.toString(),
                        getString(R.string.config_accent),
                        Palette.PALETTE
                                .map { ConfigPickerItem(it.toString(), it, colorSparse[it]) }
                                .let {
                                    ArrayList(it)
                                }
                )
                startActivityForResult(intent, REQUEST_CODE_ACCENT_COLOR)
            }
            ITEM_ABOUT -> {
                val intent = Intent(this, ConfigAboutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun updateThemeItem() {
        updateItem(ITEM_THEME, { themeMap[Config.themeName] })
    }

    private fun updateAccentColorItem() {
        updateItem(ITEM_ACCENT_COLOR, { colorSparse[Config.accentColor] })
    }

    private inline fun updateItem(itemId: Int, crossinline summaryGetter: () -> String?) {
        val position = items.indexOfFirst { it.id == itemId }
        items[position].summary = summaryGetter()

        adapter.notifyItemChanged(position)
    }

}
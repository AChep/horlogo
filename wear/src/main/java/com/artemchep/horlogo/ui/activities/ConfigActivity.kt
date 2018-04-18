package com.artemchep.horlogo.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.artemchep.horlogo.Config
import com.artemchep.horlogo.R
import com.artemchep.horlogo.model.ConfigItem
import com.artemchep.horlogo.ui.Palette
import com.artemchep.horlogo.ui.adapter.ConfigAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.util.ConfigManager
import kotlinx.android.synthetic.main.activity_config.*


/**
 * @author Artem Chepurnoy
 */
class ConfigActivity : Activity(), OnItemClickListener<ConfigItem> {

    companion object {
        private const val ITEM_COMPLICATIONS = 1
        private const val ITEM_ACCENT_COLOR = 2
        private const val ITEM_ABOUT = 3

        private const val REQUEST_CODE_ACCENT_COLOR = 101
    }

    private val configManager by lazy { ConfigManager(this) }

    private lateinit var items: MutableList<ConfigItem>

    private lateinit var adapter: ConfigAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        items = mutableListOf(
                ConfigItem(
                        id = ITEM_COMPLICATIONS,
                        icon = getDrawable(R.drawable.ic_view),
                        title = getString(R.string.config_complications)),
                ConfigItem(
                        id = ITEM_ACCENT_COLOR,
                        icon = getDrawable(R.drawable.ic_palette),
                        title = getString(R.string.config_accent)),
                ConfigItem(
                        id = ITEM_ABOUT,
                        icon = getDrawable(R.drawable.ic_information_outline),
                        title = getString(R.string.config_about))
        )

        adapter = ConfigAdapter(items).apply {
            onItemClickListener = this@ConfigActivity
        }

        setContentView(R.layout.activity_config)

        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ConfigActivity)
            setHasFixedSize(true)

            adapter = this@ConfigActivity.adapter
        }
    }

    override fun onStart() {
        super.onStart()
        configManager.start {
            updateAccentColorItem()
        }

        updateAccentColorItem()
    }

    override fun onStop() {
        configManager.stop()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ACCENT_COLOR -> {
                if (resultCode != RESULT_OK || data == null) {
                    return
                }

                val color = data.getIntExtra(ConfigAccentActivity.RESULT_COLOR, Config.accentColor)

                // Set current accent color and say that the
                // config has changed.
                Config.edit(this) {
                    accentColor = color
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
            ITEM_ACCENT_COLOR -> {
                val intent = ConfigAccentActivity.newIntent(this, Config.accentColor)
                startActivityForResult(intent, REQUEST_CODE_ACCENT_COLOR)
            }
            ITEM_ABOUT -> {
                val intent = Intent(this, ConfigAboutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun updateAccentColorItem() {
        val position = items.indexOfFirst { it.id == ITEM_ACCENT_COLOR }
        items[position].apply {
            summary = Palette.getPaletteNames(resources)[Config.accentColor]
        }

        adapter.notifyItemChanged(position)
    }

}
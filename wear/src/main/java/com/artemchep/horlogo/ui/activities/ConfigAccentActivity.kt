package com.artemchep.horlogo.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.artemchep.horlogo.Config
import com.artemchep.horlogo.R
import com.artemchep.horlogo.extensions.forEachIndexed
import com.artemchep.horlogo.model.ConfigPaletteItem
import com.artemchep.horlogo.ui.Palette
import com.artemchep.horlogo.ui.adapter.ConfigPaletteAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.activity_config.*


/**
 * @author Artem Chepurnoy
 */
class ConfigAccentActivity : Activity(), OnItemClickListener<ConfigPaletteItem> {

    companion object {
        private const val EXTRA_COLOR = "extra::color"
        const val RESULT_COLOR = "result::color"

        fun newIntent(context: Context, color: Int): Intent {
            return Intent(context, ConfigAccentActivity::class.java).apply {

                // Put previously selected color
                putExtra(EXTRA_COLOR, color)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentColor = intent!!.getIntExtra(EXTRA_COLOR, 0)
        val colors = listOf(
                Palette.RED to getString(R.string.red),
                Palette.PINK to getString(R.string.pink),
                Palette.PURPLE to getString(R.string.purple),
                Palette.DEEP_PURPLE to getString(R.string.deep_purple),
                Palette.INDIGO to getString(R.string.indigo),
                Palette.BLUE to getString(R.string.blue),
                Palette.CYAN to getString(R.string.cyan),
                Palette.TEAL to getString(R.string.teal),
                Palette.GREEN to getString(R.string.green),
                Palette.LIGHT_GREEN to getString(R.string.light_green),
                Palette.LIME to getString(R.string.lime),
                Palette.YELLOW to getString(R.string.yellow),
                Palette.AMBER to getString(R.string.amber),
                Palette.ORANGE to getString(R.string.orange),
                Palette.DEEP_ORANGE to getString(R.string.deep_orange),
                Palette.GREY to getString(R.string.grey),
                Color.WHITE to getString(R.string.white)
        )

        setContentView(R.layout.activity_config)
        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ConfigAccentActivity)
            setHasFixedSize(true)

            // Create an adapter
            adapter = ConfigPaletteAdapter(
                    colors.map { ConfigPaletteItem(it.first, it.second) }.toMutableList()
            ).apply {
                selectedColor = currentColor
                onItemClickListener = this@ConfigAccentActivity
            }
        }
    }

    override fun onItemClick(view: View, model: ConfigPaletteItem) {
        val intent = Intent().apply {
            putExtra(RESULT_COLOR, model.color)
        }

        setResult(RESULT_OK, intent)
        finishAfterTransition()
    }

}
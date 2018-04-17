package com.artemchep.horlogo.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IColorPresenter
import com.artemchep.horlogo.contracts.IColorView
import com.artemchep.horlogo.model.ConfigPaletteItem
import com.artemchep.horlogo.presenters.ColorPresenter
import com.artemchep.horlogo.ui.activities.base.ActivityBase
import com.artemchep.horlogo.ui.adapter.ConfigPaletteAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.activity_config.*


/**
 * @author Artem Chepurnoy
 */
class ConfigAccentActivity : ActivityBase<IColorView, IColorPresenter>(),
        IColorView,
        OnItemClickListener<ConfigPaletteItem> {

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

    override val view: IColorView = this

    private val models = ArrayList<ConfigPaletteItem>()

    private val adapter = ConfigPaletteAdapter(models).apply {
        onItemClickListener = this@ConfigAccentActivity
    }

    override fun createPresenter(): IColorPresenter {
        val selectedColor = intent!!.getIntExtra(EXTRA_COLOR, 0)
        return ColorPresenter(applicationContext, selectedColor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_config)
        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ConfigAccentActivity)

            adapter = this@ConfigAccentActivity.adapter
        }
    }

    override fun onItemClick(view: View, model: ConfigPaletteItem) {
        val intent = Intent().apply {
            putExtra(RESULT_COLOR, model.color)
        }

        setResult(RESULT_OK, intent)
        finishAfterTransition()
    }

    override fun showPalette(items: List<ConfigPaletteItem>, color: Int) {
        models.apply {
            clear()
            addAll(items)
        }

        adapter.apply {
            selectedColor = color
            notifyDataSetChanged()
        }
    }

}
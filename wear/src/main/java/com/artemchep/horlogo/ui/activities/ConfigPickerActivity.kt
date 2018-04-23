package com.artemchep.horlogo.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IPickerPresenter
import com.artemchep.horlogo.contracts.IPickerView
import com.artemchep.horlogo.model.ConfigPickerItem
import com.artemchep.horlogo.presenters.PickerPresenter
import com.artemchep.horlogo.ui.activities.base.ActivityBase
import com.artemchep.horlogo.ui.adapter.ConfigPickerAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.activity_config.*


/**
 * @author Artem Chepurnoy
 */
class ConfigPickerActivity : ActivityBase<IPickerView, IPickerPresenter>(),
        IPickerView,
        OnItemClickListener<ConfigPickerItem> {

    companion object {
        private const val EXTRA_KEY = "extra::key"
        private const val EXTRA_TITLE = "extra::title"
        private const val EXTRA_ITEMS = "extra::items"
        const val RESULT_KEY = "result::key"

        fun newIntent(context: Context, key: String, title: String?, items: ArrayList<ConfigPickerItem>): Intent {
            return Intent(context, ConfigPickerActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_ITEMS, items)
                putExtra(EXTRA_KEY, key)
                putExtra(EXTRA_TITLE, title)
            }
        }
    }

    override val view: IPickerView = this

    private val models = ArrayList<ConfigPickerItem>()

    private lateinit var adapter: ConfigPickerAdapter

    override fun createPresenter(): IPickerPresenter {
        val key = intent!!.getStringExtra(EXTRA_KEY)
        val items = intent!!.getParcelableArrayListExtra<ConfigPickerItem>(EXTRA_ITEMS)
        return PickerPresenter(key, items)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent!!.getStringExtra(EXTRA_TITLE)
        adapter = ConfigPickerAdapter(models, title).apply {
            onItemClickListener = this@ConfigPickerActivity
        }

        setContentView(R.layout.activity_config)
        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ConfigPickerActivity)

            adapter = this@ConfigPickerActivity.adapter
        }
    }

    override fun onItemClick(view: View, model: ConfigPickerItem) {
        val intent = Intent().apply {
            putExtra(RESULT_KEY, model.key)
        }

        setResult(RESULT_OK, intent)
        finishAfterTransition()
    }

    override fun showItems(items: List<ConfigPickerItem>, key: String?) {
        models.apply {
            clear()
            addAll(items)
        }

        adapter.apply {
            selectedKey = key
            notifyDataSetChanged()
        }
    }

}
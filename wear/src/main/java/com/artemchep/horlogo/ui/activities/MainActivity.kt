package com.artemchep.horlogo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IMainPresenter
import com.artemchep.horlogo.contracts.IMainView
import com.artemchep.horlogo.presenters.MainPresenter
import com.artemchep.horlogo.ui.activities.base.ActivityBase
import com.artemchep.horlogo.ui.adapter.MainAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.model.ConfigPickerItem
import kotlinx.android.synthetic.main.activity_config.*

/**
 * @author Artem Chepurnoy
 */
class MainActivity : ActivityBase<IMainView, IMainPresenter>(),
        IMainView,
        OnItemClickListener<ConfigItem> {

    override val view: IMainView = this

    private val models = ArrayList<ConfigItem>()

    private lateinit var adapter: MainAdapter

    override fun createPresenter() = MainPresenter(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.config)
        adapter = MainAdapter(models, title).apply {
            onItemClickListener = this@MainActivity
        }

        setContentView(R.layout.activity_config)

        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val key = data?.getStringExtra(PickerActivity.RESULT_KEY)
        presenter.result(requestCode, resultCode, key)
    }

    override fun onItemClick(view: View, model: ConfigItem) {
        with(presenter) {
            when (model.id) {
                MainPresenter.ITEM_COMPLICATIONS -> navigateTo(IMainPresenter.Destination.COMPLICATION)
                MainPresenter.ITEM_LAYOUT -> navigateTo(IMainPresenter.Destination.LAYOUT)
                MainPresenter.ITEM_THEME -> navigateTo(IMainPresenter.Destination.THEME)
                MainPresenter.ITEM_ACCENT_COLOR -> navigateTo(IMainPresenter.Destination.ACCENT)
                MainPresenter.ITEM_ABOUT -> navigateTo(IMainPresenter.Destination.ABOUT)
            }
        }
    }

    override fun showItems(items: List<ConfigItem>) {
        models.apply {
            clear()
            addAll(items)
        }

        adapter.notifyDataSetChanged()
    }

    /**
     * Updates a single item
     */
    override fun notifyItemChanged(position: Int) {
        adapter.tellItemChanged(position)
    }

    override fun showPickerScreenForResult(title: String?, key: String, items: List<ConfigPickerItem>, resultCode: Int) {
        val intent = PickerActivity.newIntent(this, key, title, ArrayList(items))
        startActivityForResult(intent, resultCode)
    }

    override fun showComplicationsScreen() {
        val intent = Intent(this, ComplicationsActivity::class.java)
        startActivity(intent)
    }

    override fun showAboutScreen() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

}
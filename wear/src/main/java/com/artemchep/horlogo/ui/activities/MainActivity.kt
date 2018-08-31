package com.artemchep.horlogo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IMainPresenter
import com.artemchep.horlogo.contracts.IMainView
import com.artemchep.horlogo.presenters.MainPresenterWear
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

    private lateinit var adapter: MainAdapter

    override fun createPresenter() = MainPresenterWear(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.config)
        adapter = MainAdapter(presenter.items, title).apply {
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
        presenter.navigateTo(model.id)
    }

    override fun notifyDataChanged() {
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
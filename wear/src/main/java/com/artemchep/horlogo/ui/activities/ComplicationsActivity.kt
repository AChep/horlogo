package com.artemchep.horlogo.ui.activities

import android.content.ComponentName
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationHelperActivity
import android.view.View
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IComplicationsPresenter
import com.artemchep.horlogo.contracts.IComplicationsView
import com.artemchep.horlogo.presenters.ComplicationsPresenter
import com.artemchep.horlogo.ui.activities.base.ActivityBase
import com.artemchep.horlogo.ui.adapter.MainAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.watchface.WatchFaceService
import kotlinx.android.synthetic.main.activity_config_complications.*


/**
 * @author Artem Chepurnoy
 */
class ComplicationsActivity : ActivityBase<IComplicationsView, IComplicationsPresenter>(),
        IComplicationsView,
        View.OnClickListener,
        OnItemClickListener<ConfigItem> {

    override val view: IComplicationsView = this

    private val models = ArrayList<ConfigItem>()

    private lateinit var adapter: MainAdapter

    override fun createPresenter() = ComplicationsPresenter(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = getString(R.string.config_complications)
        adapter = MainAdapter(models, title).apply {
            onItemClickListener = this@ComplicationsActivity
        }

        setContentView(R.layout.activity_config_complications)
        errorView.setOnClickListener(this)
        complicationsRecyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ComplicationsActivity)

            adapter = this@ComplicationsActivity.adapter
        }
    }

    override fun onClick(view: View) {
        when {
            view === errorView -> presenter.retrieveProviderInfo()
        }
    }

    override fun onItemClick(view: View, model: ConfigItem) {
        presenter.navigateToComplicationChooser(model.id)
    }

    override fun showComplicationsInfo(list: List<ConfigItem>) {
        models.apply {
            clear()
            addAll(list)
        }

        adapter.notifyDataSetChanged()

        complicationsRecyclerView.visibility = View.VISIBLE
        progressView.visibility = View.GONE
        errorView.visibility = View.GONE
    }

    override fun showError() {
        complicationsRecyclerView.visibility = View.INVISIBLE
        progressView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }

    override fun showLoader() {
        complicationsRecyclerView.visibility = View.INVISIBLE
        progressView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
    }

    override fun showComplicationChooser(complicationId: Int) {
        val supportedTypes = intArrayOf(
                ComplicationData.TYPE_RANGED_VALUE,
                ComplicationData.TYPE_ICON,
                ComplicationData.TYPE_SHORT_TEXT,
                ComplicationData.TYPE_SMALL_IMAGE
        )

        val watchFace = ComponentName(this, WatchFaceService::class.java)
        val intent = ComplicationHelperActivity.createProviderChooserHelperIntent(this, watchFace, complicationId, *supportedTypes)
        startActivity(intent)
    }

}
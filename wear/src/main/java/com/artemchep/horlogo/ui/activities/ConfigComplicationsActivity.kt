package com.artemchep.horlogo.ui.activities

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationHelperActivity
import android.view.View
import com.artemchep.horlogo.R
import com.artemchep.horlogo.model.ConfigItem
import com.artemchep.horlogo.ui.adapter.ConfigAdapter
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.watchface.WatchFaceService
import kotlinx.android.synthetic.main.activity_config.*


/**
 * @author Artem Chepurnoy
 */
class ConfigComplicationsActivity : Activity(), OnItemClickListener<ConfigItem> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        recyclerView.apply {
            isEdgeItemsCenteringEnabled = true
            layoutManager = LinearLayoutManager(this@ConfigComplicationsActivity)
            setHasFixedSize(true)

            // Create an adapter
            adapter = ConfigAdapter(mutableListOf(
                    ConfigItem(
                            id = WatchFaceService.COMPLICATION_FIRST,
                            icon = getDrawable(R.drawable.ic_box_1),
                            title = getString(R.string.config_complication_numbered, 1)),
                    ConfigItem(
                            id = WatchFaceService.COMPLICATION_SECOND,
                            icon = getDrawable(R.drawable.ic_box_2),
                            title = getString(R.string.config_complication_numbered, 2)),
                    ConfigItem(
                            id = WatchFaceService.COMPLICATION_THIRD,
                            icon = getDrawable(R.drawable.ic_box_3),
                            title = getString(R.string.config_complication_numbered, 3)),
                    ConfigItem(
                            id = WatchFaceService.COMPLICATION_FOURTH,
                            icon = getDrawable(R.drawable.ic_box_4),
                            title = getString(R.string.config_complication_numbered, 4))
            )).apply {
                onItemClickListener = this@ConfigComplicationsActivity
            }
        }
    }

    override fun onItemClick(view: View, model: ConfigItem) {
        launchComplicationHelperActivity(model.id)
    }

    private fun launchComplicationHelperActivity(id: Int) {
        val supportedTypes = intArrayOf(
                ComplicationData.TYPE_RANGED_VALUE,
                ComplicationData.TYPE_ICON,
                ComplicationData.TYPE_SHORT_TEXT,
                ComplicationData.TYPE_SMALL_IMAGE
        )

        val watchFace = ComponentName(this, WatchFaceService::class.java)
        val intent = ComplicationHelperActivity.createProviderChooserHelperIntent(this, watchFace, id, *supportedTypes)
        startActivity(intent)

    }

}
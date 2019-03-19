package com.artemchep.horlogo.sync

import android.content.Context
import android.widget.Toast
import com.artemchep.config.Config
import com.artemchep.horlogo.Cfg
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

/**
 * @author Artem Chepurnoy
 */
class CfgDataClientAdapter(private val context: Context) : Config.OnConfigChangedListener<String> {
    override fun onConfigChanged(keys: Set<String>) {
        val dataClient = Wearable.getDataClient(context)

        val request = PutDataMapRequest.create("/config")
        keys.forEach { key ->
            when (key) {
                Cfg.KEY_THEME -> request.dataMap.putString(key, Cfg.themeName)
                Cfg.KEY_ACCENT_COLOR -> request.dataMap.putInt(key, Cfg.accentColor)
                Cfg.KEY_LAYOUT -> request.dataMap.putString(key, Cfg.layoutName)
            }
        }

        // Push the changes to our
        // watch.
        val dataItem = request
            .asPutDataRequest()
            .setUrgent()
        val task = dataClient.putDataItem(dataItem)
        task.addOnCompleteListener { task ->
            val message = if (task.isSuccessful) {
                "Sync completed"
            } else {
                "Sync failed"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

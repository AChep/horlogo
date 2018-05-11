package com.artemchep.horlogo.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.artemchep.horlogo.ACTION_CONFIG_CHANGED

typealias ConfigCallback = () -> Unit

/**
 * Listens to the config-changed events and invokes the
 * callback.
 *
 * @author Artem Chepurnoy
 */
class ConfigManager(private val context: Context) {

    companion object {
        private const val TAG = "ConfigManager"

        fun broadcastConfigChanged(context: Context) {
            val lbm = LocalBroadcastManager.getInstance(context)
            lbm.sendBroadcast(Intent(ACTION_CONFIG_CHANGED))
        }
    }

    private var callback: ConfigCallback? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            callback?.invoke()
        }
    }

    fun start(callback: ConfigCallback) {
        if (this.callback != null) {
            Log.w(TAG, "Started the config manager second time!")
            return
        }

        this.callback = callback

        // Register our config receiver, don't forget to
        // call `stop()` later!
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(receiver, IntentFilter(ACTION_CONFIG_CHANGED))
    }

    fun stop() {
        if (this.callback == null) {
            Log.w(TAG, "Stopped the config manager second time!")
            return
        }

        this.callback = null

        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.unregisterReceiver(receiver)
    }

}
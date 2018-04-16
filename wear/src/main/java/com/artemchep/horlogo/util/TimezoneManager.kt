package com.artemchep.horlogo.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

typealias TimeZoneCallback = () -> Unit

/**
 * @author Artem Chepurnoy
 */
class TimezoneManager(private val context: Context) {

    companion object {
        private const val TAG = "TimezoneManager"
    }

    private var callback: TimeZoneCallback? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            callback?.invoke()
        }
    }

    fun start(callback: TimeZoneCallback) {
        if (this.callback != null) {
            Log.w(TAG, "Started the timezone manager second time!")
            return
        }

        this.callback = callback

        // Register our timezone receiver, don't forget to
        // call `stop()` later!
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
    }

    fun stop() {
        if (this.callback == null) {
            Log.w(TAG, "Stopped the timezone manager second time!")
            return
        }

        this.callback = null
        context.unregisterReceiver(receiver)
    }

}
package com.artemchep.horlogo

import android.content.Context
import android.content.SharedPreferences
import com.artemchep.horlogo.ui.Palette
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author Artem Chepurnoy
 */
object Config {

    private const val KEY_ACCENT_COLOR = "accent"

    private var editor: SharedPreferences.Editor? = null
    private var broadcasting = false

    var accentColor: Int by configProperty(KEY_ACCENT_COLOR, Palette.BLUE)

    fun init(context: Context) {
        broadcasting = true

        val map = context.getConfigSharedPreferences().all
        (map[KEY_ACCENT_COLOR] as Int?)?.also { accentColor = it }

        broadcasting = false
    }

    /**
     * Call before writing to config properties, otherwise you
     * will get a [NullPointerException].
     */
    fun edit(context: Context, block: Config.() -> Unit) {
        synchronized(this) {
            try {
                context.getConfigSharedPreferences().edit().let {
                    editor = it
                    block.invoke(this@Config)
                    it.apply()
                }
            } finally {
                editor = null
            }
        }
    }

    private fun Context.getConfigSharedPreferences(): SharedPreferences {
        return getSharedPreferences("config", 0)
    }

    private fun <T> configProperty(key: String, initialValue: T): ReadWriteProperty<Any?, T> = object : ObservableProperty<T>(initialValue) {
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (broadcasting || oldValue == newValue) {
                return
            }

            editor!!.apply {
                when (newValue) {
                    is Int -> putInt(key, newValue)
                    is Long -> putLong(key, newValue)
                    is String -> putString(key, newValue)
                }
            }
        }
    }

}
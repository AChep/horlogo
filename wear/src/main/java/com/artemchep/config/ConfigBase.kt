package com.artemchep.config

import android.content.Context
import android.content.SharedPreferences
import android.os.Looper
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

/**
 * @author Artem Chepurnoy
 */
abstract class ConfigBase {

    private var isInitializing = false

    private var editor: SharedPreferences.Editor? = null

    private val changes: MutableSet<String> = HashSet()

    private val listeners: MutableList<OnConfigChangedListener> = ArrayList()

    /**
     * Should be called on application create method,
     * only once.
     */
    fun init(context: Context) {
        val map = getConfigSharedPreferences(context).all

        // Maps the saved values to actual
        // variables.
        isInitializing = true
        onInitVars(map)
        isInitializing = false
    }

    abstract fun onInitVars(map: Map<String, *>)

    /**
     * Call before writing to config properties, otherwise you
     * will get a [NullPointerException].
     */
    fun edit(context: Context, block: () -> Unit) {
        synchronized(this) {
            try {
                getConfigSharedPreferences(context).edit().let {
                    editor = it
                    block.invoke()
                    it.apply()
                }
            } finally {
                editor = null
            }

            if (Looper.getMainLooper().isCurrentThread) {
                // Create a copy of changes
                val copy = HashSet(changes)
                changes.clear()

                // Notify about the changes
                notifyChanges(copy)
            } else throw IllegalStateException("Editing the config is not supported from other threads")
        }
    }

    private fun notifyChanges(changes: Set<String>) {
        synchronized(listeners) {
            changes.forEach { key ->
                // Notify each of the listeners
                // about this particular change
                listeners.forEach { it.onConfigChanged(key) }
            }
        }
    }

    /**
     * Adds the listener to the list of active listeners;
     * dont forget to remove it later.
     * @see removeListener
     */
    fun addListener(listener: OnConfigChangedListener): ConfigRegistration {
        synchronized(listeners) {
            listeners += listener
            return ConfigRegistration(this, listener)
        }
    }

    /**
     * Removes the listener from the list of active listeners.
     * @see addListener
     */
    fun removeListener(listener: OnConfigChangedListener) {
        synchronized(listeners) {
            listeners -= listener
        }
    }

    fun <T> configProperty(key: String, initialValue: T) = ConfigProperty(key, initialValue)

    protected abstract fun getConfigSharedPreferences(context: Context): SharedPreferences

    /**
     * @author Artem Chepurnoy
     */
    inner class ConfigProperty<T>(
            private val key: String,
            initialValue: T
    ) : ObservableProperty<T>(initialValue) {

        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
            if (isInitializing) return

            // Register the current change in the system, so
            // we know what to notify.
            changes.add(key)

            // Update the editor
            editor!!.apply {
                when (newValue) {
                    is Int -> putInt(key, newValue)
                    is Long -> putLong(key, newValue)
                    is String -> putString(key, newValue)
                }
            }
        }

    }

    /**
     * @author Artem Chepurnoy
     */
    class ConfigRegistration(
            private val config: ConfigBase,
            private val listener: OnConfigChangedListener
    ) {

        var isRegistered = true
            private set

        /**
         * Removes the listener from the Config, does nothing if
         * called second+ times.
         */
        fun unregister() {
            if (isRegistered) {
                isRegistered = false
                config.removeListener(listener)
            }
        }

    }

    /**
     * @author Artem Chepurnoy
     */
    interface OnConfigChangedListener {
        fun onConfigChanged(key: String)
    }

    /**
     * A type-safe version of [Map.getOrDefault]. Returns the value corresponding
     * to the given `key`, or `defaultValue` if such a key is not present in the map.
     */
    inline fun <reified T, K> Map<K, *>.getOrDefaultTs(key: K, defaultValue: T) = this[key] as? T
            ?: defaultValue

}
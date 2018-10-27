package com.artemchep.config.store.common

import android.content.SharedPreferences
import com.artemchep.config.store.StoreRead

/**
 * @author Artem Chepurnoy
 */
class SharedPrefStoreRead(private val sharedPref: SharedPreferences) : StoreRead<String> {

    override fun getBoolean(key: String, defaultValue: Boolean) =
        sharedPref.getBoolean(key, defaultValue)

    override fun getString(key: String, defaultValue: String): String =
        sharedPref.getString(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long): Long =
        sharedPref.getLong(key, defaultValue)

    override fun getInt(key: String, defaultValue: Int): Int = sharedPref.getInt(key, defaultValue)

}
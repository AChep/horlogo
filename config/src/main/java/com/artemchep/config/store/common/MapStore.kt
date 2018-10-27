package com.artemchep.config.store.common

import com.artemchep.config.store.Store

/**
 * @author Artem Chepurnoy
 */
class MapStore<K>(private val map: MutableMap<K, Any>) : Store<K> {

    private val storeRead = MapStoreRead(map)

    override fun putBoolean(key: K, value: Boolean) {
        map[key] = value
    }

    override fun putString(key: K, value: String) {
        map[key] = value
    }

    override fun putLong(key: K, value: Long) {
        map[key] = value
    }

    override fun putInt(key: K, value: Int) {
        map[key] = value
    }

    override fun getBoolean(key: K, defaultValue: Boolean) = storeRead.getBoolean(key, defaultValue)

    override fun getString(key: K, defaultValue: String): String =
        storeRead.getString(key, defaultValue)

    override fun getLong(key: K, defaultValue: Long): Long = storeRead.getLong(key, defaultValue)

    override fun getInt(key: K, defaultValue: Int): Int = storeRead.getInt(key, defaultValue)

}
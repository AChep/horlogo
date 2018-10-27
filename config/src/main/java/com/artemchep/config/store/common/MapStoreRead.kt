package com.artemchep.config.store.common

import com.artemchep.config.store.StoreRead

/**
 * @author Artem Chepurnoy
 */
class MapStoreRead<K>(private val map: Map<K, *>) : StoreRead<K> {

    override fun getBoolean(key: K, defaultValue: Boolean) = map.getOrDefaultTs(key, defaultValue)

    override fun getString(key: K, defaultValue: String): String =
        map.getOrDefaultTs(key, defaultValue)

    override fun getLong(key: K, defaultValue: Long): Long = map.getOrDefaultTs(key, defaultValue)

    override fun getInt(key: K, defaultValue: Int): Int = map.getOrDefaultTs(key, defaultValue)

    /**
     * A type-safe version of [Map.getOrDefault]. Returns the value corresponding
     * to the given `key`, or `defaultValue` if such a key is not present in the map.
     */
    private inline fun <reified T, K> Map<K, *>.getOrDefaultTs(key: K, defaultValue: T) =
        this[key] as? T
            ?: defaultValue

}
package com.artemchep.config.store

/**
 * @author Artem Chepurnoy
 * @see Store
 * @see StoreWrite
 */
interface StoreRead<K> {

    fun getBoolean(key: K, defaultValue: Boolean): Boolean

    fun getString(key: K, defaultValue: String): String

    fun getLong(key: K, defaultValue: Long): Long

    fun getInt(key: K, defaultValue: Int): Int

}
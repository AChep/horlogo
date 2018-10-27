package com.artemchep.config.store

/**
 * @author Artem Chepurnoy
 * @see StoreRead
 * @see Store
 */
interface StoreWrite<K> {

    fun putBoolean(key: K, value: Boolean)

    fun putString(key: K, value: String)

    fun putLong(key: K, value: Long)

    fun putInt(key: K, value: Int)

}
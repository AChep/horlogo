package com.artemchep.config.editor.common

import com.artemchep.config.editor.Editor
import com.artemchep.config.store.common.MapStore

/**
 * Editor that stores configuration values in
 * a map.
 *
 * @author Artem Chepurnoy
 */
class MapEditor<K>(map: MutableMap<K, Any>) : Editor<K> {

    private val store = MapStore(map)

    /*
     * Changes are written to the map immediately,
     * no need to apply them.
     */
    override fun apply() {
    }

    override fun putBoolean(key: K, value: Boolean) = store.putBoolean(key, value)

    override fun putString(key: K, value: String) = store.putString(key, value)

    override fun putLong(key: K, value: Long) = store.putLong(key, value)

    override fun putInt(key: K, value: Int) = store.putInt(key, value)

}
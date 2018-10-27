package com.artemchep.config.editor.common

import com.artemchep.config.editor.Editor

/**
 * Editor that does nothing.
 *
 * @author Artem Chepurnoy
 */
class EmptyEditor<K> : Editor<K> {

    override fun apply() = Unit

    override fun putBoolean(key: K, value: Boolean) = Unit

    override fun putString(key: K, value: String) = Unit

    override fun putLong(key: K, value: Long) = Unit

    override fun putInt(key: K, value: Int) = Unit

}
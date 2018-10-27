package com.artemchep.config.editor

import com.artemchep.config.store.StoreWrite

/**
 * @author Artem Chepurnoy
 */
interface Editor<K> : StoreWrite<K> {

    fun apply()

}

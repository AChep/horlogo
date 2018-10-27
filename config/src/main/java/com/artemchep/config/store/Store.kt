package com.artemchep.config.store

/**
 * @author Artem Chepurnoy
 * @see StoreRead
 * @see StoreWrite
 */
interface Store<K> : StoreRead<K>, StoreWrite<K>

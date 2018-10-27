package com.artemchep.config.extensions

import com.artemchep.config.store.Store
import com.artemchep.config.store.StoreRead
import com.artemchep.config.store.common.MapStore
import com.artemchep.config.store.common.MapStoreRead

fun <K> Map<K, *>.asStore(): StoreRead<K> = MapStoreRead(this)

fun <K> MutableMap<K, Any>.asStore(): Store<K> = MapStore(this)

package com.artemchep.config.extensions

import android.content.SharedPreferences
import com.artemchep.config.store.StoreRead
import com.artemchep.config.store.common.SharedPrefStoreRead

fun SharedPreferences.asStore(): StoreRead<String> = SharedPrefStoreRead(this)

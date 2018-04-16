package com.artemchep.horlogo.extensions

import android.util.SparseArray

inline fun <T> SparseArray<T>.forEachIndexed(block: (index : Int, value: T) -> Unit) {
    val size = this.size()
    for (i in 0 until size) {
        val value = this.valueAt(i)
        block(i, value)
    }
}
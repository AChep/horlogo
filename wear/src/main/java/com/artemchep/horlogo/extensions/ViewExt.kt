package com.artemchep.horlogo.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children


/**
 * Finds the view in the hierarchy that is right
 * below given `x` and `y`.
 *
 * Please note, that this method is not perfect and differs
 * from built-in one.
 */
fun View.findViewByLocation(x: Int, y: Int): View? {
    return if (x in left..right && y in top..bottom) {
        // If the view is a group, then check all its
        // children first.
        if (this is ViewGroup) {
            children.forEach {
                val newX = x - left
                val newY = y - top
                it.findViewByLocation(newX, newY)?.also { return it }
            }
        }

        // We could not find a child, so return a parent.
        this
    } else null
}
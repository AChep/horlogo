package com.artemchep.horlogo.extensions

import android.view.View
import android.widget.TextView

/**
 * [Sets text][TextView.setText] to TextView and, if the text is `null` or empty,
 * hides the view.
 */
fun <T : TextView> T.setTextExclusive(text: String?) = setTextExclusive(text, this)

fun <T : TextView> T.setTextExclusive(text: String?, view: View) {
    this.text = text
    view.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
}

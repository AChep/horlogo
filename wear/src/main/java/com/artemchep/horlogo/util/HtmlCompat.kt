package com.artemchep.horlogo.util

import android.os.Build
import android.text.Html

fun fromHtml(src: String) : CharSequence {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(src, Html.FROM_HTML_MODE_COMPACT)
    } else Html.fromHtml(src)
}
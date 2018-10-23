package com.artemchep.horlogo.ui.model

import android.graphics.drawable.Drawable

/**
 * @author Artem Chepurnoy
 */
data class ConfigItem(
    val id: Int,
    var icon: Drawable? = null,
    val title: String,
    var summary: String? = null
)

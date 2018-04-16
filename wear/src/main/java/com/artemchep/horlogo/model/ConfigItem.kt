package com.artemchep.horlogo.model

import android.graphics.drawable.Drawable

fun List<ConfigItem>.findById(id: Int) : Pair<Int, ConfigItem>? {
    forEachIndexed { index, configItem ->
        if (configItem.id == id) return index to configItem
    }
    return null
}

/**
 * @author Artem Chepurnoy
 */
data class ConfigItem(
        val id: Int,
        val icon: Drawable? = null,
        val title: String,
        var summary: String? = null
)

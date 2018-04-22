package com.artemchep.horlogo.ui

import android.graphics.Color

/**
 * @author Artem Chepurnoy
 */
enum class Theme(
        val backgroundColor: Int,
        val clockMinuteColor: Int,
        val complicationColor: Int
) {

    BLACK(
            backgroundColor = Color.BLACK,
            clockMinuteColor = Color.GRAY,
            complicationColor = Color.LTGRAY),
    DARK(
            backgroundColor = 0xFF212121.toInt(),
            clockMinuteColor = Color.GRAY,
            complicationColor = Color.LTGRAY),
    LIGHT(
            backgroundColor = 0xFFF5F5F5.toInt(),
            clockMinuteColor = Color.GRAY,
            complicationColor = Color.DKGRAY);

}
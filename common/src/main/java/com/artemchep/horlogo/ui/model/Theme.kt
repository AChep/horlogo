package com.artemchep.horlogo.ui.model

import android.graphics.Color

/**
 * @author Artem Chepurnoy
 */
data class Theme(
    /**
     * The color of background of
     * the watch-face.
     */
    var backgroundColor: Int = 0,
    var complicationColor: Int = 0,
    var clockHourColor: Int = 0,
    var clockMinuteColor: Int = 0
) {

    companion object {
        val BLACK = Theme(
            backgroundColor = Color.BLACK,
            clockHourColor = 0xFF546E7A.toInt(),
            clockMinuteColor = Color.GRAY,
            complicationColor = Color.LTGRAY
        )
        val DARK = Theme(
            backgroundColor = 0xFF212121.toInt(),
            clockHourColor = 0xFF546E7A.toInt(),
            clockMinuteColor = Color.GRAY,
            complicationColor = Color.LTGRAY
        )
        val LIGHT = Theme(
            backgroundColor = 0xFFF5F5F5.toInt(),
            clockHourColor = 0xFF78909C.toInt(),
            clockMinuteColor = Color.GRAY,
            complicationColor = Color.DKGRAY
        )
    }

}
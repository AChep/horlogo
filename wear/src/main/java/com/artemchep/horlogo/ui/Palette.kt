package com.artemchep.horlogo.ui

import android.graphics.Color
import android.support.annotation.ColorInt

/**
 * @author Artem Chepurnoy
 */
object Palette {

    @ColorInt
    const val RED = -0xbbcca
    @ColorInt
    const val PINK = -0x16e19d
    @ColorInt
    const val PURPLE = -0x63d850
    @ColorInt
    const val DEEP_PURPLE = -0x98c549
    @ColorInt
    const val INDIGO = -0xc0ae4b
    @ColorInt
    const val BLUE = -0xde690d
    @ColorInt
    const val CYAN = -0xff6859
    @ColorInt
    const val TEAL = -0xff6978
    @ColorInt
    const val GREEN = -0xbc5fb9
    @ColorInt
    const val LIGHT_GREEN = -0x743cb6
    @ColorInt
    const val LIME = -0x3223c7
    @ColorInt
    const val YELLOW = -0x14c5
    @ColorInt
    const val AMBER = -0x3ef9
    @ColorInt
    const val ORANGE = -0x6800
    @ColorInt
    const val DEEP_ORANGE = -0xa8de
    @ColorInt
    const val BROWN = -0x86aab8
    @ColorInt
    const val GREY = -0x616162

    val PALETTE = intArrayOf(
            RED, PINK, PURPLE, DEEP_PURPLE,
            INDIGO, BLUE, CYAN, TEAL, GREEN,
            LIGHT_GREEN, LIME, YELLOW, AMBER,
            ORANGE, DEEP_ORANGE, BROWN, GREY,
            Color.WHITE)

}

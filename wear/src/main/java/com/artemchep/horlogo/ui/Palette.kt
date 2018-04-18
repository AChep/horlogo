package com.artemchep.horlogo.ui

import android.content.res.Resources
import android.graphics.Color
import android.support.annotation.ColorInt
import android.util.SparseArray
import com.artemchep.horlogo.R
import java.lang.ref.Reference
import java.lang.ref.SoftReference

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

    private var sPaletteNamesRef : Reference<SparseArray<String>>? = null

    fun getPaletteNames(resources : Resources) : SparseArray<String> {
        return sPaletteNamesRef?.get() ?: SparseArray<String>().apply {
            with(resources) {
                put(Palette.RED, getString(R.string.red))
                put(Palette.PINK, getString(R.string.pink))
                put(Palette.PURPLE, getString(R.string.purple))
                put(Palette.DEEP_PURPLE, getString(R.string.deep_purple))
                put(Palette.INDIGO, getString(R.string.indigo))
                put(Palette.BLUE, getString(R.string.blue))
                put(Palette.CYAN, getString(R.string.cyan))
                put(Palette.TEAL, getString(R.string.teal))
                put(Palette.GREEN, getString(R.string.green))
                put(Palette.LIGHT_GREEN, getString(R.string.light_green))
                put(Palette.LIME, getString(R.string.lime))
                put(Palette.YELLOW, getString(R.string.yellow))
                put(Palette.AMBER, getString(R.string.amber))
                put(Palette.ORANGE, getString(R.string.orange))
                put(Palette.DEEP_ORANGE, getString(R.string.deep_orange))
                put(Palette.BROWN, getString(R.string.brown))
                put(Palette.GREY, getString(R.string.grey))
                put(Color.WHITE, getString(R.string.white))
            }
        }.also {
            sPaletteNamesRef = SoftReference(it)
        }
    }

}

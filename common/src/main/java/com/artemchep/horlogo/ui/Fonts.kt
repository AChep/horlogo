package com.artemchep.horlogo.ui

import android.content.res.AssetManager
import android.graphics.Typeface
import java.util.*

/**
 * @author Artem Chepurnoy
 */
object Fonts {

    /**
     * The Overlock font by Dario Manuel Muhafara;
     * https://fonts.google.com/specimen/Overlock
     */
    const val OVERLOCK_REGULAR = "Overlock-Regular"

    private val FONTS = HashMap<String, Typeface>()

    fun getTypeface(assetManager: AssetManager, name: String = OVERLOCK_REGULAR): Typeface {
        return FONTS
            .getOrPut(name) {
                Typeface.createFromAsset(assetManager, "fonts/$name.ttf")
            }
    }

}
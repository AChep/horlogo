package com.artemchep.horlogo.ui.drawables

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * Simple drawable that actually draws nothing and should be
 * used as a placeholder.
 *
 * @author Artem Chepurnoy
 */
class EmptyDrawable : Drawable() {

    override fun draw(p0: Canvas?) {
    }

    override fun setColorFilter(p0: ColorFilter?) {
    }

    override fun setAlpha(p0: Int) {
    }

    override fun getOpacity(): Int = PixelFormat.TRANSPARENT

}
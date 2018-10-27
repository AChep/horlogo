package com.artemchep.horlogo.ui.watchface

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.support.wearable.complications.ComplicationData

/**
 * @author Artem Chepurnoy
 */
data class Complication(
    var normalIconDrawable: Drawable? = null,
    var ambientIconDrawable: Drawable? = null,
    var longMsg: CharSequence? = null,
    var shortMsg: CharSequence? = null,
    var isActive: Boolean = false,

    /**
     * Reference to source complication data, that was used to
     * create this instance.
     */
    val raw: ComplicationData
) {

    fun refreshActive() {
        val now = SystemClock.currentThreadTimeMillis()
        isActive = raw.isActive(now)
    }

    /**
     * Refreshes the message of this complication according to
     * the time change.
     */
    fun refreshMessage(context: Context) {
        val now = SystemClock.currentThreadTimeMillis()
        val longText = raw.longText?.getText(context, now)
        val longTitle = raw.longTitle?.getText(context, now)
        val shortText = raw.shortText?.getText(context, now)
        val shortTitle = raw.shortTitle?.getText(context, now)

        // Update the text
        longMsg = joinTitleAndText(longTitle, longText)
        shortMsg = joinTitleAndText(shortTitle, shortText)
    }

    private fun joinTitleAndText(title: CharSequence?, text: CharSequence?): CharSequence? {
        return if (text != null && title != null) {
            // Join text with title
            "$text $title"
        } else text ?: title
    }

}

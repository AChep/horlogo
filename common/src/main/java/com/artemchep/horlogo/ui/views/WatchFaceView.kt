package com.artemchep.horlogo.ui.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.view.isVisible
import com.artemchep.horlogo.*
import com.artemchep.horlogo.ui.drawables.EmptyDrawable
import com.artemchep.horlogo.ui.model.Theme
import com.artemchep.horlogo_common.R
import java.util.*

/**
 * @author Artem Chepurnoy
 */
class WatchFaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var dividerTextView: TextView? = null

    private lateinit var hourTextView: TextView
    private lateinit var minuteTextView: TextView
    private lateinit var complication1TextView: TextView
    private lateinit var complication2TextView: TextView
    private lateinit var complication3TextView: TextView
    private lateinit var complication4TextView: TextView

    private lateinit var iconEmpty: Drawable

    private var iconSize: Int = 0

    override fun onFinishInflate() {
        super.onFinishInflate()
        hourTextView = findViewById(R.id.hour)
        minuteTextView = findViewById(R.id.minute)
        dividerTextView = findViewById(R.id.divider)

        complication1TextView = findViewById(R.id.complication1)
        complication2TextView = findViewById(R.id.complication2)
        complication3TextView = findViewById(R.id.complication3)
        complication4TextView = findViewById(R.id.complication4)

        WATCH_COMPLICATIONS.forEach { id ->
            findComplicationViewById(id).tag = id
        }

        iconSize = context.resources.getDimensionPixelSize(R.dimen.watch_face_icon_size)
        iconEmpty = EmptyDrawable().applyIconBounds()
    }

    override fun hasOverlappingRendering(): Boolean = false

    /**
     * Enables or disables the anti-aliasing of all of the
     * text views.
     */
    fun setAntiAlias(isEnabled: Boolean) {
        listOf(
            hourTextView, minuteTextView,
            complication1TextView,
            complication2TextView,
            complication3TextView,
            complication4TextView
        ).forEach {
            it.paint.isAntiAlias = isEnabled
        }
    }

    fun setTheme(theme: Theme) {
        setBackgroundColor(theme.backgroundColor)
        hourTextView.setTextColor(theme.clockHourColor)
        minuteTextView.setTextColor(theme.clockMinuteColor)
        dividerTextView?.setTextColor(theme.clockMinuteColor)

        // Set complications color
        val tintList = ColorStateList.valueOf(theme.complicationColor)
        listOf(
            complication1TextView,
            complication2TextView,
            complication3TextView,
            complication4TextView
        ).forEach {
            it.setTextColor(theme.complicationColor)
            it.compoundDrawableTintList = tintList
        }
    }

    fun setTime(calendar: Calendar) {
        minuteTextView.text = formatTwoDigitNumber(calendar.get(Calendar.MINUTE))
        hourTextView.text = formatTwoDigitNumber(if (DateFormat.is24HourFormat(context)) {
            calendar.get(Calendar.HOUR_OF_DAY)
        } else calendar.get(Calendar.HOUR).takeIf { it != 0 } ?: 12)
    }

    /**
     * Formats number as two-digit number: adds leading zero if
     * needed.
     */
    private fun formatTwoDigitNumber(n: Int) = if (n <= 9) "0$n" else "$n"

    /**
     * Sets the complication icon to start of the view
     * @see setComplicationContentText
     */
    fun setComplicationIcon(id: Int, icon: Drawable?) {
        findComplicationViewById(id).apply {
            val drawable = icon?.applyIconBounds() ?: iconEmpty
            this.setCompoundDrawables(drawable, null, null, null)
        }
    }

    /**
     * Sets the complication text, or hides the view
     * if text is `null`.
     * @see setComplicationIcon
     */
    fun setComplicationContentText(id: Int, text: CharSequence?) {
        findComplicationViewById(id).apply {
            this.isVisible = !text.isNullOrEmpty()
            this.text = text
        }
    }

    private fun findComplicationViewById(id: Int) = when (id) {
        WATCH_COMPLICATION_FIRST -> complication1TextView
        WATCH_COMPLICATION_SECOND -> complication2TextView
        WATCH_COMPLICATION_THIRD -> complication3TextView
        WATCH_COMPLICATION_FOURTH -> complication4TextView
        else -> throw IllegalArgumentException()
    }

    /**
     * Applies the {0, 0, [iconSize], [iconSize]}
     * bounds to the drawable.
     */
    private fun Drawable.applyIconBounds(): Drawable {
        setBounds(0, 0, iconSize, iconSize)
        return this
    }

}
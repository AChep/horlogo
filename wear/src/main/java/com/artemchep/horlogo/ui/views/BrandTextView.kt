package com.artemchep.horlogo.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.artemchep.horlogo.ui.Fonts

/**
 * @author Artem Chepurnoy
 */
class BrandTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    init {
        typeface = Fonts.getTypeface(context.assets, Fonts.OVERLOCK_REGULAR)
    }

}
package com.artemchep.horlogo.ui.activities

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Html
import com.artemchep.horlogo.R
import kotlinx.android.synthetic.main.activity_config_about.*
import java.util.*


/**
 * @author Artem Chepurnoy
 */
class ConfigAboutActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_about)

        contentTextView.text = getContentMessage()
    }

    private fun getContentMessage(): CharSequence {
        val calendar = Calendar.getInstance()
        val src = getString(R.string.dialog_about_message, calendar.get(Calendar.YEAR))
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(src, Html.FROM_HTML_MODE_COMPACT)
        } else Html.fromHtml(src)
    }

}
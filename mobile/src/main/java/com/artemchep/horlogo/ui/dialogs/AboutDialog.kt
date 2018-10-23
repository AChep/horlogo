package com.artemchep.horlogo.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IAboutPresenter
import com.artemchep.horlogo.contracts.IAboutView
import com.artemchep.horlogo.presenters.AboutPresenter
import com.artemchep.horlogo.ui.Fonts
import com.artemchep.horlogo.ui.dialogs.base.DialogBase

/**
 * @author Artem Chepurnoy
 */
class AboutDialog : DialogBase<IAboutView, IAboutPresenter>(), IAboutView {

    companion object {
        private const val EMPTY_TEXT = ""
    }

    override val view: IAboutView = this

    private lateinit var titleView: TextView
    private lateinit var contentView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val md = MaterialDialog.Builder(context!!)
            .title(EMPTY_TEXT) // to inject content view into layout
            .content(EMPTY_TEXT) // to inject content view into layout
            .negativeText(R.string.action_close)
            .build()
        titleView = md.titleView.apply {
            setOnClickListener {
                presenter.navigateTo(IAboutPresenter.Destination.BUILD_INFO)
            }
            // Apply the branded look to
            // title.
            typeface = Fonts.getTypeface(context!!.assets)
        }
        contentView = md.contentView!!
        return md
    }

    override fun createPresenter(): IAboutPresenter = AboutPresenter(context!!)

    override fun setTitleText(title: CharSequence) {
        titleView.text = title
    }

    override fun setContentText(content: CharSequence) {
        contentView.text = content
    }

}
package com.artemchep.horlogo.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IAboutPresenter
import com.artemchep.horlogo.contracts.IAboutView
import com.artemchep.horlogo.presenters.AboutPresenter
import com.artemchep.horlogo.ui.activities.base.ActivityBase
import kotlinx.android.synthetic.main.activity_config_about.*

/**
 * @author Artem Chepurnoy
 */
class AboutActivity : ActivityBase<IAboutView, IAboutPresenter>(), IAboutView, View.OnClickListener {

    override val view: IAboutView = this

    override fun createPresenter() = AboutPresenter(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config_about)

        titleTextView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.titleTextView -> presenter.navigateTo(IAboutPresenter.Destination.BUILD_INFO)
        }
    }

    override fun setTitleText(title: CharSequence) {
        titleTextView.text = title
    }

    override fun setContentText(content: CharSequence) {
        contentTextView.text = content
    }

}
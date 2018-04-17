package com.artemchep.horlogo.presenters

import android.content.Context
import android.content.pm.PackageManager
import com.artemchep.horlogo.BuildConfig
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IAboutPresenter
import com.artemchep.horlogo.contracts.IAboutView
import com.artemchep.horlogo.util.fromHtml


/**
 * @author Artem Chepurnoy
 */
class AboutPresenter(private val context: Context) : IAboutPresenter {

    override var view: IAboutView? = null

    override fun onStart() {
        super.onStart()
        view!!.apply {
            setTitleText(getTitleText())
            setContentText(getContentText())
        }
    }

    private fun getTitleText(): CharSequence {
        val appName = context.getString(R.string.app_name)
        val versionName = try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = info.versionName

            // Make the info part of version name a bit smaller
            if (versionName.indexOf('-') >= 0) {
                versionName.replaceFirst("-".toRegex(), "<small>-") + "</small>"
            } else versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "N/A"
        }

        val src = context.getString(R.string.about_title, appName, versionName)
        return fromHtml(src)
    }

    private fun getContentText(): CharSequence {
        val src = context.getString(R.string.about_content, BuildConfig.MY_TIME_YEAR)
        return fromHtml(src)
    }

    override fun showBuildInfo() {
        view!!.showToast(BuildConfig.MY_TIME)
    }

}

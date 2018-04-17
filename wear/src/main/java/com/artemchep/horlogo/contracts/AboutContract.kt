package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView

/**
 * @author Artem Chepurnoy
 */
interface IAboutView : IView<IAboutView, IAboutPresenter> {

    fun setTitleText(title: CharSequence)

    fun setContentText(content: CharSequence)

    fun showToast(text: CharSequence)

}

/**
 * @author Artem Chepurnoy
 */
interface IAboutPresenter : IPresenter<IAboutPresenter, IAboutView> {

    fun showBuildInfo()

}

package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.interfaces.Toastable

/**
 * @author Artem Chepurnoy
 */
interface IAboutView : IView<IAboutView, IAboutPresenter>, Toastable {

    /**
     * Sets the title text.
     */
    fun setTitleText(title: CharSequence)

    /**
     * Sets the content text.
     */
    fun setContentText(content: CharSequence)

}

/**
 * @author Artem Chepurnoy
 */
interface IAboutPresenter : IPresenter<IAboutPresenter, IAboutView> {

    fun navigateTo(dst: Destination)

    /**
     * @author Artem Chepurnoy
     */
    enum class Destination {
        BUILD_INFO,
    }

}

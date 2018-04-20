package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.model.ConfigItem

/**
 * @author Artem Chepurnoy
 */
interface IComplicationsView : IView<IComplicationsView, IComplicationsPresenter> {

    fun showComplicationsInfo(list: List<ConfigItem>)

    fun showError()

    fun showLoader()

}

/**
 * @author Artem Chepurnoy
 */
interface IComplicationsPresenter : IPresenter<IComplicationsPresenter, IComplicationsView> {

    fun retrieveProviderInfo()

}

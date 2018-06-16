package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.ui.model.ConfigItem

/**
 * @author Artem Chepurnoy
 */
interface IComplicationsView : IView<IComplicationsView, IComplicationsPresenter> {

    fun showComplicationsInfo(list: List<ConfigItem>)

    fun showError()

    fun showLoader()

    /**
     * Shows a chooser that allows to pick specific complication for
     * a complication id.
     */
    fun showComplicationChooser(complicationId: Int)

}

/**
 * @author Artem Chepurnoy
 */
interface IComplicationsPresenter : IPresenter<IComplicationsPresenter, IComplicationsView> {

    fun retrieveProviderInfo()

    fun navigateToComplicationChooser(complicationId: Int)

}

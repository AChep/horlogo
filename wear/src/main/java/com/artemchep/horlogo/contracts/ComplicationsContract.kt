package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.model.ConfigItem

/**
 * @author Artem Chepurnoy
 */
interface IComplicationsView : IView<IComplicationsView, IComplicationsPresenter> {

    var items : MutableList<ConfigItem>

    fun notifyItemChanged(index: Int)

    fun notifyItemsChanged()

}

/**
 * @author Artem Chepurnoy
 */
interface IComplicationsPresenter : IPresenter<IComplicationsPresenter, IComplicationsView> {
}

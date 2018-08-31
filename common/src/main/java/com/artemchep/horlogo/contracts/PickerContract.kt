package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.ui.model.ConfigPickerItem

/**
 * @author Artem Chepurnoy
 */
interface IPickerView : IView<IPickerView, IPickerPresenter> {

    fun showItems(items: List<ConfigPickerItem>, key: String?)

}

/**
 * @author Artem Chepurnoy
 */
interface IPickerPresenter : IPresenter<IPickerPresenter, IPickerView> {
}

package com.artemchep.horlogo.presenters

import com.artemchep.horlogo.contracts.IPickerPresenter
import com.artemchep.horlogo.contracts.IPickerView
import com.artemchep.horlogo.ui.model.ConfigPickerItem

/**
 * @author Artem Chepurnoy
 */
class PickerPresenter(
    private val key: String?,
    private val items: List<ConfigPickerItem>
) : IPickerPresenter {

    override var view: IPickerView? = null

    override fun onStart() {
        super.onStart()
        view!!.showItems(items, key)
    }

}
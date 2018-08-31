package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.model.ConfigPickerItem

/**
 * @author Artem Chepurnoy
 */
interface IMainView : IView<IMainView, IMainPresenter> {

    fun notifyDataChanged()

    fun notifyItemChanged(position: Int)

    fun showPickerScreenForResult(title: String?, key: String, items: List<ConfigPickerItem>, resultCode: Int)

    fun showComplicationsScreen()

    fun showAboutScreen()

}

/**
 * @author Artem Chepurnoy
 */
interface IMainPresenter : IPresenter<IMainPresenter, IMainView> {

    val items: MutableList<ConfigItem>

    fun result(requestCode: Int, resultCode: Int, key: String?)

    fun navigateTo(itemId: Int)

    companion object {
        const val ITEM_COMPLICATIONS = 1
        const val ITEM_LAYOUT = 2
        const val ITEM_THEME = 3
        const val ITEM_ACCENT_COLOR = 4
        const val ITEM_ABOUT = 5
    }

}

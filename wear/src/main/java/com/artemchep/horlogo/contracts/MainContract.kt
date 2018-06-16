package com.artemchep.horlogo.contracts

import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.model.ConfigPickerItem

/**
 * @author Artem Chepurnoy
 */
interface IMainView : IView<IMainView, IMainPresenter> {

    fun showItems(items: List<ConfigItem>)

    fun notifyItemChanged(position: Int)

    fun showPickerScreenForResult(title: String?, key: String, items: List<ConfigPickerItem>, resultCode: Int)

    fun showComplicationsScreen()

    fun showAboutScreen()

}

/**
 * @author Artem Chepurnoy
 */
interface IMainPresenter : IPresenter<IMainPresenter, IMainView> {

    fun result(requestCode: Int, resultCode: Int, key: String?)

    fun navigateTo(dst: Destination)

    /**
     * @author Artem Chepurnoy
     */
    enum class Destination {
        COMPLICATION,
        LAYOUT,
        THEME,
        ACCENT,
        ABOUT,
    }

}

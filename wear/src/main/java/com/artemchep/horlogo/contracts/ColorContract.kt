package com.artemchep.horlogo.contracts

import android.support.annotation.ColorInt
import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.model.ConfigPaletteItem

/**
 * @author Artem Chepurnoy
 */
interface IColorView : IView<IColorView, IColorPresenter> {

    fun showPalette(items: List<ConfigPaletteItem>, @ColorInt color: Int)

}

/**
 * @author Artem Chepurnoy
 */
interface IColorPresenter : IPresenter<IColorPresenter, IColorView> {
}

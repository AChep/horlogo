package com.artemchep.horlogo.presenters

import android.content.Context
import com.artemchep.horlogo.contracts.IColorPresenter
import com.artemchep.horlogo.contracts.IColorView
import com.artemchep.horlogo.model.ConfigPaletteItem
import com.artemchep.horlogo.ui.Palette

/**
 * @author Artem Chepurnoy
 */
class ColorPresenter(context: Context, private val selectedColor: Int) : IColorPresenter {

    override var view: IColorView? = null

    private val items: List<ConfigPaletteItem>

    init {
        val map = Palette.getPaletteNames(context.resources)
        items = Palette.PALETTE
                .map { ConfigPaletteItem(it, map[it]) }
                .toList()
    }

    override fun onStart() {
        super.onStart()
        view!!.showPalette(items, selectedColor)
    }

}
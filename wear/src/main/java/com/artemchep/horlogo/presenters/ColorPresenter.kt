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
        val items = ArrayList<ConfigPaletteItem>(map.size())

        // Map sparse array of colors to list of
        // palette items.
        for (i in 0 until map.size()) {
            val color = map.keyAt(i)
            val name = map.valueAt(i)
            items.add(ConfigPaletteItem(color, name))
        }

        this.items = items.toList()
    }

    override fun onStart() {
        super.onStart()
        view!!.showPalette(items, selectedColor)
    }

}
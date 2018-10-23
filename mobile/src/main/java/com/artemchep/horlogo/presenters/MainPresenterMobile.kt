package com.artemchep.horlogo.presenters

import android.content.Context
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IMainPresenter
import com.artemchep.horlogo.ui.model.ConfigItem

/**
 * @author Artem Chepurnoy
 */
class MainPresenterMobile(context: Context) : MainPresenter(context) {

    override val items: MutableList<ConfigItem> = mutableListOf(
        ConfigItem(
            id = IMainPresenter.ITEM_LAYOUT,
            icon = getDrawable(R.drawable.ic_view_carousel),
            title = getString(R.string.config_layout)
        ),
        ConfigItem(
            id = IMainPresenter.ITEM_THEME,
            icon = getDrawable(R.drawable.ic_invert_colors),
            title = getString(R.string.config_theme)
        ),
        ConfigItem(
            id = IMainPresenter.ITEM_ACCENT_COLOR,
            icon = getDrawable(R.drawable.ic_palette),
            title = getString(R.string.config_accent)
        )
    )

}
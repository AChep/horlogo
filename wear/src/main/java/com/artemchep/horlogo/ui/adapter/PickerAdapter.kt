package com.artemchep.horlogo.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.luminance
import com.artemchep.horlogo.R
import com.artemchep.horlogo.ui.adapters.AdapterBase
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.model.ConfigPickerItem

/**
 * @author Artem Chepurnoy
 */
open class PickerAdapter(
        models: MutableList<ConfigPickerItem>,
        title: CharSequence?
) :
        AdapterTitled<ConfigPickerItem, PickerAdapter.Holder>(models, title) {

    override val binderItem = object : Binder<Holder>() {

        override fun createView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View {
            return inflater.inflate(R.layout.item_color, parent, false)
        }

        override fun createViewHolder(itemView: View, viewType: Int): Holder {
            return Holder(itemView, this@PickerAdapter)
        }

        override fun bindViewHolder(holder: Holder, position: Int) {
            val model = getItem(position)
            val colorIsDark = model.color.luminance < 0.5f
            val colorContent = if (colorIsDark) Color.WHITE else Color.BLACK

            holder.apply {
                itemView.setBackgroundColor(model.color)
                titleTextView.text = model.title
                titleTextView.setTextColor(colorContent)
                checkImageView.apply {
                    visibility = if (model.key == selectedKey) {
                        imageTintList = ColorStateList.valueOf(colorContent)
                        View.VISIBLE
                    } else View.INVISIBLE
                }
            }
        }

    }

    var selectedKey: String? = null

    /**
     * @author Artem Chepurnoy
     */
    class Holder(
            view: View,
            listener: OnItemClickListener<Int>
    ) : AdapterBase.ViewHolderBase(view, listener), View.OnClickListener {

        internal val checkImageView = view.findViewById<ImageView>(R.id.iconImageView)
        internal val titleTextView = view.findViewById<TextView>(R.id.titleTextView)

        init {
            view.setOnClickListener(this)
        }
    }

}
package com.artemchep.horlogo.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.artemchep.horlogo.R
import com.artemchep.horlogo.model.ConfigPaletteItem
import com.artemchep.horlogo.ui.Palette
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
open class ConfigPaletteAdapter(
        models: MutableList<ConfigPaletteItem>
) :
        AdapterBase<ConfigPaletteItem, ConfigPaletteAdapter.Holder>(models) {

    var selectedColor = 0

    /**
     * @author Artem Chepurnoy
     */
    class Holder(
            listener: OnItemClickListener<Int>,
            view: View
    ) : AdapterBase.ViewHolderBase(listener, view), View.OnClickListener {

        internal val checkImageView = view.findViewById<ImageView>(R.id.iconImageView)
        internal val titleTextView = view.findViewById<TextView>(R.id.titleTextView)

        init {
            view.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_color, parent, false)
        return Holder(this, v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = models[position]
        val colorIsDark = ColorUtils.calculateLuminance(model.color) < 0.5f
        val colorContent = if (colorIsDark) Color.WHITE else Color.BLACK

        holder.apply {
            itemView.setBackgroundColor(model.color)
            titleTextView.text = model.title
            titleTextView.setTextColor(colorContent)
            checkImageView.apply {
                visibility = if (model.color == selectedColor) {
                    imageTintList = ColorStateList.valueOf(colorContent)
                    View.VISIBLE
                } else View.INVISIBLE
            }
        }
    }

}
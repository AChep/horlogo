package com.artemchep.horlogo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.artemchep.horlogo.R
import com.artemchep.horlogo.extensions.setTextExclusive
import com.artemchep.horlogo.ui.adapters.AdapterBase
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.model.ConfigItem

/**
 * @author Artem Chepurnoy
 */
open class MainAdapter(
        models: MutableList<ConfigItem>,
        title: CharSequence?
) : AdapterTitled<ConfigItem, MainAdapter.Holder>(models, title) {

    override val binderItem = object : Binder<Holder>(){

        override fun createView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View {
            return inflater.inflate(R.layout.item_config, parent, false)
        }

        override fun createViewHolder(itemView: View, viewType: Int): Holder {
            return Holder(itemView, this@MainAdapter)
        }

        override fun bindViewHolder(holder: Holder, position: Int) {
            val model = getItem(position)
            holder.apply {
                titleTextView.text = model.title
                summaryTextView.setTextExclusive(model.summary)
                iconImageView.setImageDrawable(model.icon)
            }
        }

    }

    /**
     * @author Artem Chepurnoy
     */
    class Holder(
            view: View,
            listener: OnItemClickListener<Int>
    ) : AdapterBase.ViewHolderBase(view, listener), View.OnClickListener {

        internal val iconImageView = view.findViewById<ImageView>(R.id.iconImageView)
        internal val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        internal val summaryTextView = view.findViewById<TextView>(R.id.summaryTextView)

        init {
            view.setOnClickListener(this)
        }
    }

}
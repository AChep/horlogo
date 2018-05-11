package com.artemchep.horlogo.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.artemchep.horlogo.R
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
abstract class AdapterTitled<M, H : RecyclerView.ViewHolder>(
        models: MutableList<M>,
        private val title: CharSequence?
) :
        AdapterBase<M, RecyclerView.ViewHolder>(models) {

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_ITEM = 2
    }

    private val offset = title?.let { 1 } ?: 0

    /**
     * @author Artem Chepurnoy
     */
    class TitleHolder(
            listener: OnItemClickListener<Int>,
            view: View
    ) : AdapterBase.ViewHolderBase(listener, view) {

        internal val titleTextView = view.findViewById<TextView>(R.id.titleTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TITLE -> {
                val v = inflater.inflate(R.layout.item_title, parent, false)
                TitleHolder(this, v)
                        .apply {
                            titleTextView.text = title
                        }
            }
            else -> onCreateItemViewHolder(parent, viewType)
        }
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): H

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        when (type) {
            TYPE_TITLE -> {
            }
            else -> onBindItemViewHolder(holder as H, position, models[position - offset])
        }
    }

    abstract fun onBindItemViewHolder(holder: H, position: Int, model: M)

    override fun onItemClick(view: View, model: Int) {
        onItemClickListener?.onItemClick(view, models[model - offset])
    }

    override fun getItemViewType(position: Int): Int {
        return if (title != null && position == 0) {
            TYPE_TITLE
        } else TYPE_ITEM
    }

    override fun getItemCount(): Int = super.getItemCount() + offset

}
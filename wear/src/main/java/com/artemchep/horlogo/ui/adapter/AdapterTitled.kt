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
        /**
         * List of models to be shown. Changing this list in future will
         * affect the adapter, so don't forget to notify it about data
         * set change.
         */
        models: MutableList<M>,
        private val title: CharSequence?
) : AdapterBase<M, RecyclerView.ViewHolder>(models) {

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_ITEM = 2
    }

    override val binder: Binder<RecyclerView.ViewHolder> = object : Binder<RecyclerView.ViewHolder>() {

        override fun createView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View {
            return when (viewType) {
                TYPE_TITLE -> binderTitle.createView(inflater, parent, viewType)
                else -> binderItem.createView(inflater, parent, viewType)
            }
        }

        override fun createViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                TYPE_TITLE -> binderTitle.createViewHolder(itemView, viewType)
                else -> binderItem.createViewHolder(itemView, viewType)
            }
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewType = getItemViewType(position)
            when (viewType) {
                TYPE_TITLE -> {
                }
                else -> {
                    @Suppress("UNCHECKED_CAST")
                    binderItem.bindViewHolder(holder as H, position)
                }
            }
        }

    }

    /**
     * Binder for the rest of items
     * @see binder
     * @see binderTitle
     */
    abstract val binderItem: Binder<H>

    /**
     * Binder for the single title item
     * @see binder
     * @see binderItem
     */
    private val binderTitle: Binder<RecyclerView.ViewHolder> = object : Binder<RecyclerView.ViewHolder>() {

        override fun createView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View {
            return inflater.inflate(R.layout.item_title, parent, false)
        }

        override fun createViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder {
            return TitleViewHolder(this@AdapterTitled, itemView)
                    .apply {
                        titleTextView.text = title
                    }
        }

        override fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

    }

    private val offset = title?.let { 1 } ?: 0

    override fun getItemViewType(position: Int): Int {
        return if (offset != 0 && position == 0) {
            TYPE_TITLE
        } else TYPE_ITEM
    }

    override fun getItemCount(): Int = super.getItemCount() + offset

    override fun getItem(position: Int): M = models[position - offset]

    override fun tellItemChanged(position: Int, payload: Any?) {
        notifyItemChanged(position + offset, payload)
    }

    override fun tellItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        notifyItemRangeChanged(positionStart + offset, itemCount, payload)
    }

    /**
     * View holder for simple title item.
     *
     * @author Artem Chepurnoy
     */
    private class TitleViewHolder(
            listener: OnItemClickListener<Int>,
            view: View
    ) : AdapterBase.ViewHolderBase(listener, view) {

        internal val titleTextView = view.findViewById<TextView>(R.id.titleTextView)

    }

}
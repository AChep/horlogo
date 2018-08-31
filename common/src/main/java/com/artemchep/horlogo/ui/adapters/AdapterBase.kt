package com.artemchep.horlogo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
abstract class AdapterBase<M, H : RecyclerView.ViewHolder>(
        /**
         * List of models to be shown. Changing this list in future will
         * affect the adapter, so don't forget to notify it about data
         * set change.
         */
        val models: MutableList<M>
) : RecyclerView.Adapter<H>(), OnItemClickListener<Int> {

    /**
     * On item click listener, that is being invoked on click
     * on item.
     */
    var onItemClickListener: OnItemClickListener<M>? = null

    protected abstract val binder: Binder<H>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        val inflater = LayoutInflater.from(parent.context!!)
        val view = binder.createView(inflater, parent, viewType)
        return binder.createViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        binder.bindViewHolder(holder, position)
    }

    override fun onItemClick(view: View, model: Int) {
        val item = getItem(model)
        onItemClickListener?.onItemClick(view, item)
    }

    override fun getItemCount(): Int = models.size

    protected open fun getItem(position: Int) = models[position]

    /**
     * @author Artem Chepurnoy
     */
    open class ViewHolderBase(
            itemView: View,
            private val listener: OnItemClickListener<Int>
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position >= 0) {
                listener.onItemClick(view, position)
            }
        }

    }

    open fun tellItemChanged(position: Int, payload: Any? = null) {
        notifyItemChanged(position, payload)
    }

    open fun tellItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any? = null) {
        notifyItemRangeChanged(positionStart, itemCount, payload)
    }

    /**
     * @author Artem Chepurnoy
     */
    abstract class Binder<H : RecyclerView.ViewHolder> {

        abstract fun createView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View

        abstract fun createViewHolder(itemView: View, viewType: Int): H

        abstract fun bindViewHolder(holder: H, position: Int)

    }

}
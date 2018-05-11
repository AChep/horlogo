package com.artemchep.horlogo.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener

/**
 * @author Artem Chepurnoy
 */
abstract class AdapterBase<M, H : RecyclerView.ViewHolder>(
        protected val models: MutableList<M>
) : RecyclerView.Adapter<H>(), OnItemClickListener<Int> {

    var onItemClickListener: OnItemClickListener<M>? = null

    override fun onItemClick(view: View, model: Int) {
        onItemClickListener?.onItemClick(view, models[model])
    }

    override fun getItemCount(): Int = models.size

    /**
     * @author Artem Chepurnoy
     */
    open class ViewHolderBase(
            private val listener: OnItemClickListener<Int>,
            itemView: View
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position >= 0) {
                listener.onItemClick(view, position)
            }
        }

    }

}
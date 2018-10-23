package com.artemchep.horlogo.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.artemchep.horlogo.R
import com.artemchep.horlogo.ui.Fonts
import com.artemchep.horlogo.ui.model.ConfigPickerItem

/**
 * @author Artem Chepurnoy
 */
class PickerDialog : DialogFragment() {

    companion object {
        const val TAG = "PickerDialog"

        private val EXTRA_KEY = "extra::key"
        private val EXTRA_TITLE = "extra::title"
        private val EXTRA_ITEMS = "extra::items"
        private val EXTRA_RC = "extra::rc"

        fun create(
            requestCode: Int,
            key: String,
            title: String?,
            items: ArrayList<ConfigPickerItem>
        ): PickerDialog {
            return PickerDialog().apply {
                val bundle = Bundle().apply {
                    putParcelableArrayList(EXTRA_ITEMS, items)
                    putString(EXTRA_KEY, key)
                    putString(EXTRA_TITLE, title)
                    putInt(EXTRA_RC, requestCode)
                }

                arguments = bundle
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args = arguments!!

        val key = args.getString(EXTRA_KEY)
        val requestCode = args.getInt(EXTRA_RC)
        val items = args.getParcelableArrayList<ConfigPickerItem>(EXTRA_ITEMS)!!

        val md = MaterialDialog.Builder(context!!)
            .title(args.getString(EXTRA_TITLE))
            .items(items.map { it.title })
            .itemsCallback { dialog, itemView, position, text ->
                val a = activity
                if (a is PickerDialogCallback) {
                    a.onSingleItemPicked(requestCode, Activity.RESULT_OK, items[position].key)
                }
            }
            .negativeText(R.string.action_close)
            .build()
        md.titleView.apply {
            // Apply the branded look to
            // title.
            typeface = Fonts.getTypeface(requireContext().assets)
        }
        return md
    }

    /**
     * @author Artem Chepurnoy
     */
    interface PickerDialogCallback {

        fun onSingleItemPicked(requestCode: Int, resultCode: Int, key: String?)

    }

}
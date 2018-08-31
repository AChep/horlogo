package com.artemchep.horlogo.ui.dialogs.base

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.interfaces.Toastable

/**
 * @author Artem Chepurnoy
 */
abstract class DialogBase<V : IView<V, P>, P : IPresenter<P, V>> : DialogFragment(), Toastable {

    abstract val view: V

    lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
        presenter.view = view
    }

    abstract fun createPresenter(): P

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onStop() {
        presenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.view = null
        super.onDestroy()
    }

    override fun showToast(text: CharSequence) {
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

}
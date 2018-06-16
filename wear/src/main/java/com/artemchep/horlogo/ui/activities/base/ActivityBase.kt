package com.artemchep.horlogo.ui.activities.base

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.toast
import com.artemchep.horlogo.IPresenter
import com.artemchep.horlogo.IView
import com.artemchep.horlogo.interfaces.Toastable

/**
 * @author Artem Chepurnoy
 */
abstract class ActivityBase<V : IView<V, P>, P : IPresenter<P, V>> : Activity(), Toastable {

    abstract val view: V

    lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = lastNonConfigurationInstance as P? ?: createPresenter()

        // Connect view and presenter with
        // each other
        view.presenter = presenter
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

    override fun onRetainNonConfigurationInstance(): Any = presenter

    override fun showToast(text: CharSequence) {
        toast(text, Toast.LENGTH_SHORT)
    }

}
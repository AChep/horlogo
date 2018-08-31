package com.artemchep.horlogo

/**
 * @author Artem Chepurnoy
 */
interface IView<V : IView<V, P>, P : IPresenter<P, V>> {

    var presenter: P

}

/**
 * @author Artem Chepurnoy
 */
interface IPresenter<P : IPresenter<P, V>, V : IView<V, P>> {

    var view: V?

    /**
     * Called every time the view starts, the view is guaranteed to be not null starting at this
     * method, until [onStop] is called.
     */
    fun onStart() {
    }

    fun onResume() {
    }

    fun onPause() {
    }

    /**
     * Called every time the view stops.
     * After this method, the view may be null.
     */
    fun onStop() {
    }

}

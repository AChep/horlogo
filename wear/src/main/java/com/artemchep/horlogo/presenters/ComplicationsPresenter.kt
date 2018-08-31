package com.artemchep.horlogo.presenters

import android.content.ComponentName
import android.content.Context
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderInfoRetriever
import android.util.SparseBooleanArray
import com.artemchep.horlogo.*
import com.artemchep.horlogo.contracts.IComplicationsPresenter
import com.artemchep.horlogo.contracts.IComplicationsView
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.watchface.WatchFaceService
import java.util.concurrent.Executors

private typealias Data = Pair<Int, ComplicationProviderInfo?>

/**
 * @author Artem Chepurnoy
 */
class ComplicationsPresenter(private val context: Context) : IComplicationsPresenter {

    override var view: IComplicationsView? = null

    private lateinit var providerInfoRetriever: ProviderInfoRetriever

    private val emptyModelIcon = context.getDrawable(R.drawable.ic_plus)

    private val models = mutableListOf(
            ConfigItem(
                    id = WATCH_COMPLICATION_FIRST,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_first_line)),
            ConfigItem(
                    id = WATCH_COMPLICATION_SECOND,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_second_line)),
            ConfigItem(
                    id = WATCH_COMPLICATION_THIRD,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_third_line)),
            ConfigItem(
                    id = WATCH_COMPLICATION_FOURTH,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_fourth_line))
    )

    private var providerInfoBucket: ComplicationProviderInfoBucket? = null

    /**
     * `true` if it's a first time we loading the complication
     * info, `false` otherwise.
     */
    private var firstLoad = true

    private var lastFailed = false

    override fun onStart() {
        super.onStart()
        providerInfoRetriever = ProviderInfoRetriever(context, Executors.newCachedThreadPool())
        providerInfoRetriever.init()

        view!!.showComplicationsInfo(models)
    }

    override fun onResume() {
        super.onResume()
        retrieveProviderInfo()
    }

    override fun retrieveProviderInfo() {
        if (firstLoad || lastFailed) {
            view!!.showLoader()
        }

        providerInfoBucket?.cancel()
        providerInfoBucket = ComplicationProviderInfoBucket(object : ComplicationProviderInfoBucket.Callback {
            override fun onProviderInfoReceived(list: List<Data>) {
                firstLoad = false
                lastFailed = false

                list.forEach { (watchFaceComplicationId, info) ->
                    val index = models.indexOfFirst { it.id == watchFaceComplicationId }
                    models[index].apply {
                        icon = info?.providerIcon?.loadDrawable(context) ?: emptyModelIcon
                        summary = info?.providerName
                    }
                }

                view!!.showComplicationsInfo(models)
            }

            override fun onRetrievalFailed() {
                firstLoad = false
                lastFailed = true

                models.forEach {
                    it.icon = emptyModelIcon
                    it.summary = null
                }

                view!!.showError()
            }
        }, *WATCH_COMPLICATIONS)

        val watchFaceComponentName = ComponentName(context, WatchFaceService::class.java)
        providerInfoRetriever.retrieveProviderInfo(providerInfoBucket, watchFaceComponentName, *WATCH_COMPLICATIONS)
    }

    override fun onPause() {
        super.onPause()
        providerInfoBucket?.cancel()
    }

    override fun onStop() {
        try {
            providerInfoRetriever.release()
        } catch (e: Exception) {
            // Can happen if we failed to bind to
            // the internal service
            e.printStackTrace()
        }

        super.onStop()
    }

    override fun navigateToComplicationChooser(complicationId: Int) {
        view!!.showComplicationChooser(complicationId)
    }

    /**
     * @author Artem Chepurnoy
     */
    private class ComplicationProviderInfoBucket(
            private var callback: Callback?,
            vararg ids: Int)
        : ProviderInfoRetriever.OnProviderInfoReceivedCallback() {

        private val data = ArrayList<Data>()
        private val check = SparseBooleanArray().apply {
            ids.forEach {
                put(it, false)
            }
        }

        /**
         * @author Artem Chepurnoy
         */
        interface Callback {

            fun onProviderInfoReceived(list: List<Data>)

            fun onRetrievalFailed()

        }

        override fun onProviderInfoReceived(watchFaceComplicationId: Int, info: ComplicationProviderInfo?) {
            check.put(watchFaceComplicationId, true)
            data += watchFaceComplicationId to info

            // Check if we retrieved all of the complication provider
            // info
            val size = check.size()
            for (i in 0 until size) {
                if (!check.valueAt(i)) return
            }

            callback?.onProviderInfoReceived(data)
        }

        override fun onRetrievalFailed() {
            callback?.onRetrievalFailed()
        }

        /**
         * After calling this method the callback is guaranteed to
         * not be called.
         */
        fun cancel() {
            callback = null
        }

    }

}

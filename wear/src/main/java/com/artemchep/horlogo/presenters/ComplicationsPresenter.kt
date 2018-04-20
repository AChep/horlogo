package com.artemchep.horlogo.presenters

import android.content.ComponentName
import android.content.Context
import android.support.wearable.complications.ComplicationProviderInfo
import android.support.wearable.complications.ProviderInfoRetriever
import android.util.Log
import com.artemchep.horlogo.R
import com.artemchep.horlogo.contracts.IComplicationsPresenter
import com.artemchep.horlogo.contracts.IComplicationsView
import com.artemchep.horlogo.model.ConfigItem
import com.artemchep.horlogo.ui.watchface.WatchFaceService
import com.artemchep.horlogo.ui.watchface.WatchFaceService.Companion.COMPLICATIONS
import com.artemchep.horlogo.ui.watchface.WatchFaceService.Companion.COMPLICATION_FIRST
import com.artemchep.horlogo.ui.watchface.WatchFaceService.Companion.COMPLICATION_FOURTH
import com.artemchep.horlogo.ui.watchface.WatchFaceService.Companion.COMPLICATION_SECOND
import com.artemchep.horlogo.ui.watchface.WatchFaceService.Companion.COMPLICATION_THIRD
import java.util.concurrent.Executors

/**
 * @author Artem Chepurnoy
 */
class ComplicationsPresenter(private val context: Context) : IComplicationsPresenter {

    companion object {
        private const val TAG = "ComplicationsPresenter"
    }

    override var view: IComplicationsView? = null
        set(value) {
            field = value

            // Immediately bind the models of items to
            // view. See [notifyItemChanged, notifyItemsChanged]
            value?.items = models
        }

    private lateinit var providerInfoRetriever: ProviderInfoRetriever

    private val emptyModelIcon = context.getDrawable(R.drawable.ic_plus)

    private val models = mutableListOf(
            ConfigItem(
                    id = COMPLICATION_FIRST,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_first_line)),
            ConfigItem(
                    id = COMPLICATION_SECOND,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_second_line)),
            ConfigItem(
                    id = COMPLICATION_THIRD,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_third_line)),
            ConfigItem(
                    id = COMPLICATION_FOURTH,
                    icon = context.getDrawable(R.drawable.ic_plus),
                    title = context.getString(R.string.config_complication_fourth_line))
    )

    /**
     * Represents the amount of not retrieved [COMPLICATIONS] at
     * current moment of time.
     *
     * It may vary from `0` to [COMPLICATIONS.size()].
     */
    private var providerInfoLoadingCounter = 0

    private val providerInfoRetrieverCallback = object : ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
        override fun onProviderInfoReceived(watchFaceComplicationId: Int, info: ComplicationProviderInfo?) {
            Log.d(TAG, "Complication data update: id=$watchFaceComplicationId, info=$info")

            providerInfoLoadingCounter--
            assert(providerInfoLoadingCounter >= 0)

            val index = models.indexOfFirst { it.id == watchFaceComplicationId }
            models[index].apply {
                icon = info?.providerIcon?.loadDrawable(context) ?: emptyModelIcon
                summary = info?.providerName
            }

            // Update the view
            view?.apply {
                if (providerInfoLoadingCounter == 0) {
                    notifyItemsChanged()
                    setLoadingIndicatorShown(false)
                }
            }
        }

        override fun onRetrievalFailed() {
            Log.w(TAG, "Complication data retrieval failed")

            models.forEach {
                it.icon = emptyModelIcon
                it.summary = null
            }

            // Update the view
            view?.notifyItemsChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        providerInfoRetriever = ProviderInfoRetriever(context, Executors.newCachedThreadPool())
        providerInfoRetriever.init()
    }

    override fun onResume() {
        super.onResume()

        providerInfoLoadingCounter = COMPLICATIONS.size
        view!!.setLoadingIndicatorShown(true)

        // Ask provider info retriever to retrieve current
        // complications data
        val componentName = ComponentName(context, WatchFaceService::class.java)
        providerInfoRetriever.retrieveProviderInfo(providerInfoRetrieverCallback, componentName, *COMPLICATIONS)
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

}
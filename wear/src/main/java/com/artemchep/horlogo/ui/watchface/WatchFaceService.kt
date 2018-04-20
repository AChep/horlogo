package com.artemchep.horlogo.ui.watchface

import android.content.res.AssetManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.wearable.complications.ComplicationData
import android.support.wearable.watchface.CanvasWatchFaceService
import android.text.format.DateFormat
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import com.artemchep.horlogo.Config
import com.artemchep.horlogo.extensions.forEachIndexed
import com.artemchep.horlogo.util.ConfigManager
import com.artemchep.horlogo.util.TimezoneManager
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max


/**
 * @author Artem Chepurnoy
 */
class WatchFaceService : CanvasWatchFaceService() {

    companion object {
        private const val TAG = "WatchFaceService"

        private val TYPEFACE_LIGHT = Typeface.create("sans-serif-light", Typeface.NORMAL)
        private val TYPEFACE_MEDIUM = Typeface.create("sans-serif-condensed", Typeface.NORMAL)

        const val COMPLICATION_FIRST = 101
        const val COMPLICATION_SECOND = 102
        const val COMPLICATION_THIRD = 103
        const val COMPLICATION_FOURTH = 104

        /**
         * Array of IDs of all complications: [COMPLICATION_FIRST], [COMPLICATION_SECOND],
         * [COMPLICATION_THIRD], [COMPLICATION_FOURTH].
         */
        val COMPLICATIONS = intArrayOf(
                COMPLICATION_FIRST,
                COMPLICATION_SECOND,
                COMPLICATION_THIRD,
                COMPLICATION_FOURTH)

        private val FONTS = HashMap<String, Typeface>()

        fun getTypeface(assetManager: AssetManager, name: String): Typeface {
            return FONTS[name]
                    ?: Typeface
                            .createFromAsset(assetManager, "fonts/$name.ttf")
                            .also {
                                FONTS[name] = it
                            }
        }
    }

    override fun onCreateEngine() = WatchFaceEngine()

    /**
     * @author Artem Chepurnoy
     */
    open inner class WatchFaceEngine : CanvasWatchFaceService.Engine() {

        private lateinit var timeZoneManager: TimezoneManager
        private lateinit var configManager: ConfigManager

        private val calendar = Calendar.getInstance()

        /** Maps complication ids to corresponding complications data */
        private val complicationDataSparse = SparseArray<ProcessedData>()

        /** Density of the screen */
        private var density: Float = resources.displayMetrics.density

        //
        // PAINTS
        //

        private val hourPaint = Paint().apply {
            color = Config.accentColor
            textSize = 110f
            textScaleX = 0.92f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
            typeface = getTypeface(assets, "Overlock-Regular")
        }

        private val minutePaint = Paint(hourPaint).apply {
            color = Color.GRAY
        }

        private val complicationsPaint = Paint().apply {
            color = Color.LTGRAY
            textSize = 23f
            typeface = TYPEFACE_MEDIUM
            isAntiAlias = true
        }

        override fun onCreate(holder: SurfaceHolder?) {
            super.onCreate(holder)
            val context = applicationContext
            timeZoneManager = TimezoneManager(context)
            configManager = ConfigManager(context)

            setActiveComplications(*COMPLICATIONS)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                configManager.start {
                    loadConfig()
                }

                loadConfig()

                timeZoneManager.start {
                    calendar.timeZone = TimeZone.getDefault()
                    onTimeTick()
                }

                onTimeTick()
            } else {
                configManager.stop()
                timeZoneManager.stop()
            }
        }

        private fun loadConfig() {
            hourPaint.color = Config.accentColor
        }

        override fun onTimeTick() {
            super.onTimeTick()
            val now = System.currentTimeMillis()
            calendar.timeInMillis = now

            complicationDataSparse.forEachIndexed { _, value ->
                val shortText = value.raw.shortText?.getText(this@WatchFaceService, now)
                val shortTitle = value.raw.shortTitle?.getText(this@WatchFaceService, now)

                value.text = if (shortText != null || shortTitle != null) {
                    "${shortText ?: ""} ${shortTitle ?: ""}"
                } else null
            }

            invalidate()
        }

        override fun onComplicationDataUpdate(watchFaceComplicationId: Int, data: ComplicationData?) {
            super.onComplicationDataUpdate(watchFaceComplicationId, data)
            Log.d(TAG, "Complication data update: id=$watchFaceComplicationId, data=$data")

            if (data == null || (data.shortText == null && data.shortTitle == null)) {
                complicationDataSparse.remove(watchFaceComplicationId)
            } else {
                val pd = ProcessedData(
                        iconDrawable = data.icon
                                ?.loadDrawable(this@WatchFaceService)
                                ?.apply { setTint(complicationsPaint.color) },
                        ambientIconDrawable = data.burnInProtectionIcon
                                ?.loadDrawable(this@WatchFaceService)
                                ?.apply { setTint(complicationsPaint.color) },
                        raw = data
                )

                complicationDataSparse.put(watchFaceComplicationId, pd)
            }

            onTimeTick()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            super.onDraw(canvas, bounds)
            val is24Hour = DateFormat.is24HourFormat(this@WatchFaceService)
            val hh = formatTwoDigitNumber(if (is24Hour) {
                calendar.get(Calendar.HOUR_OF_DAY)
            } else {
                var hour = calendar.get(Calendar.HOUR)
                if (hour == 0) {
                    hour = 12
                }
                hour
            })
            val mm = formatTwoDigitNumber(calendar.get(Calendar.MINUTE))

            val margin = 6 * density
            val itemSize = 20 * density

            canvas.drawColor(Color.BLACK)

            // Calculate the bounds of both hours
            // and minutes
            val hhBounds = Rect()
            val mmBounds = Rect()
            val tmpBounds = Rect()
            hourPaint.getTextBounds(hh, 0, hh.length, hhBounds)
            minutePaint.getTextBounds(mm, 0, mm.length, mmBounds)

            // Draw the clock
            canvas.drawText(hh,
                    bounds.exactCenterX() - margin,
                    bounds.exactCenterY() - margin,
                    hourPaint)
            canvas.drawText(mm,
                    bounds.exactCenterX() - margin,
                    bounds.exactCenterY() + mmBounds.height() + margin,
                    minutePaint)

            // Calculate the bounds of complications
            val complicationsCount = complicationDataSparse.size()
            val complicationsBlockHeight = complicationsCount * itemSize + max(complicationsCount - 1, 0) * margin

            complicationDataSparse.forEachIndexed { i, value ->
                value.iconDrawable?.apply {
                    setBounds(
                            (bounds.exactCenterX() + 2 * margin).toInt(),
                            (bounds.exactCenterY() - complicationsBlockHeight / 2f + itemSize * i + margin * i).toInt(),
                            (bounds.exactCenterX() + 2 * margin + itemSize).toInt(),
                            (bounds.exactCenterY() - complicationsBlockHeight / 2f + itemSize * (i + 1) + margin * i).toInt()
                    )
                    draw(canvas)
                }

                value.text?.also {
                    complicationsPaint.getTextBounds(it, 0, it.length, tmpBounds)
                    canvas.drawText(it, 0, it.length,
                            bounds.exactCenterX() + 3 * margin + itemSize,
                            bounds.exactCenterY() - complicationsBlockHeight / 2f + itemSize * (i + 1) + margin * i - (itemSize - tmpBounds.height()) / 2 - margin / 5,
                            complicationsPaint)
                }
            }
        }

        /**
         * Formats number as two-digit number: adds leading zero if
         * needed.
         */
        private fun formatTwoDigitNumber(n: Int) = if (n <= 9) "0$n" else "$n"

    }

    /**
     * @author Artem Chepurnoy
     */
    data class ProcessedData(
            val iconDrawable: Drawable?,
            val ambientIconDrawable: Drawable?,
            var text: String? = null,
            val raw: ComplicationData
    )

}
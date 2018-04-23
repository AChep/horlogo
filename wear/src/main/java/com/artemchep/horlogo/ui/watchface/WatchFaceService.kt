package com.artemchep.horlogo.ui.watchface

import android.content.res.AssetManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.wearable.complications.ComplicationData
import android.support.wearable.watchface.CanvasWatchFaceService
import android.text.format.DateFormat
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import com.artemchep.horlogo.Config
import com.artemchep.horlogo.extensions.contains
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

        private val paintManager: PaintManager

        init {
            val hourPaint = Paint().apply {
                color = Color.WHITE
                textSize = 110f
                textScaleX = 0.92f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
                typeface = getTypeface(assets, "Overlock-Regular")
            }

            val minutePaint = Paint(hourPaint).apply {
                color = Color.GRAY
            }

            val complicationsPaint = Paint().apply {
                color = Color.LTGRAY
                textSize = 23f
                typeface = TYPEFACE_MEDIUM
                isAntiAlias = true
            }

            paintManager = PaintManager(
                    PaintHolder(Color.BLACK, hourPaint, minutePaint, complicationsPaint),
                    // Ambient mode paint; does not change over time
                    PaintHolder(
                            Color.BLACK,
                            Paint(hourPaint).apply {
                                isAntiAlias = false
                            },
                            Paint(minutePaint).apply {
                                isAntiAlias = false
                            },
                            Paint(complicationsPaint).apply {
                                isAntiAlias = false
                            }
                    ),
                    { holder, event ->
                        if (event contains PaintManager.EVENT_PRIMARY) {
                            // Refresh the tint color of icons
                            complicationDataSparse.forEachIndexed { _, value ->
                                value.ambientIconDrawable?.setTint(holder.complicationsPaint.color)
                                value.iconDrawable?.setTint(holder.complicationsPaint.color)
                            }
                        }
                    }
            )

        }

        override fun onCreate(holder: SurfaceHolder?) {
            super.onCreate(holder)
            val context = applicationContext
            timeZoneManager = TimezoneManager(context)
            configManager = ConfigManager(context)

            setActiveComplications(*COMPLICATIONS)
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            paintManager.isAmbientMode = inAmbientMode
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
            val theme = Config.theme
            paintManager.apply {
                setAccentColor(Config.accentColor)
                setBackgroundColor(theme.backgroundColor)
                setSecondaryColor(theme.clockMinuteColor)
                setPrimaryColor(theme.complicationColor)
            }
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
                                ?.apply { setTint(paintManager.normalPaint.complicationsPaint.color) },
                        ambientIconDrawable = data.burnInProtectionIcon
                                ?.loadDrawable(this@WatchFaceService)
                                ?.apply { setTint(paintManager.normalPaint.complicationsPaint.color) },
                        raw = data
                )

                complicationDataSparse.put(watchFaceComplicationId, pd)
            }

            onTimeTick()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            super.onDraw(canvas, bounds)
            val holder = paintManager.holder

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

            canvas.drawColor(holder.backgroundColor)

            // Calculate the bounds of both hours
            // and minutes
            val hhBounds = Rect()
            val mmBounds = Rect()
            val tmpBounds = Rect()
            holder.hourPaint.getTextBounds(hh, 0, hh.length, hhBounds)
            holder.minutePaint.getTextBounds(mm, 0, mm.length, mmBounds)

            // Draw the clock
            canvas.drawText(hh,
                    bounds.exactCenterX() - margin,
                    bounds.exactCenterY() - margin,
                    holder.hourPaint)
            canvas.drawText(mm,
                    bounds.exactCenterX() - margin,
                    bounds.exactCenterY() + mmBounds.height() + margin,
                    holder.minutePaint)

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
                    holder.complicationsPaint.getTextBounds(it, 0, it.length, tmpBounds)
                    canvas.drawText(it, 0, it.length,
                            bounds.exactCenterX() + 3 * margin + itemSize,
                            bounds.exactCenterY() - complicationsBlockHeight / 2f + itemSize * (i + 1) + margin * i - (itemSize - tmpBounds.height()) / 2 - margin / 5,
                            holder.complicationsPaint)
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

    /**
     * @author Artem Chepurnoy
     */
    class PaintManager(
            val normalPaint: PaintHolder,
            val ambientPaint: PaintHolder,
            val callback: (PaintHolder, Int) -> Unit
    ) {

        companion object {
            const val EVENT_BACKGROUND = 1
            const val EVENT_ACCENT = 2
            const val EVENT_PRIMARY = 4
            const val EVENT_SECONDARY = 8

            /** An event that contains all possible events */
            private const val EVENT_ALL = EVENT_BACKGROUND or
                    EVENT_ACCENT or
                    EVENT_PRIMARY or
                    EVENT_SECONDARY
        }

        var isAmbientMode = false
            set(value) {
                field = value
                callback(holder, EVENT_ALL)
            }

        val holder: PaintHolder
            get() = if (isAmbientMode) ambientPaint else normalPaint

        /**
         * Sets the background color of the watch face
         */
        fun setBackgroundColor(@ColorInt color: Int) {
            normalPaint.backgroundColor = color
            callback(holder, EVENT_BACKGROUND)
        }

        fun setAccentColor(@ColorInt color: Int) {
            normalPaint.hourPaint.color = color
            callback(holder, EVENT_ACCENT)
        }

        fun setPrimaryColor(@ColorInt color: Int) {
            normalPaint.complicationsPaint.color = color
            callback(holder, EVENT_PRIMARY)
        }

        fun setSecondaryColor(@ColorInt color: Int) {
            normalPaint.minutePaint.color = color
            callback(holder, EVENT_SECONDARY)
        }

    }

    /**
     * @author Artem Chepurnoy
     */
    class PaintHolder(
            var backgroundColor: Int,
            val hourPaint: Paint,
            val minutePaint: Paint,
            val complicationsPaint: Paint
    )

}
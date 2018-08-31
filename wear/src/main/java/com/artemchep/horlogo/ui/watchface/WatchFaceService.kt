package com.artemchep.horlogo.ui.watchface

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.support.wearable.complications.ComplicationData
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import androidx.core.util.forEach
import com.artemchep.config.Config
import com.artemchep.horlogo.Cfg
import com.artemchep.horlogo.R
import com.artemchep.horlogo.WATCH_COMPLICATIONS
import com.artemchep.horlogo.extensions.findViewByLocation
import com.artemchep.horlogo.ui.model.Theme
import com.artemchep.horlogo.ui.views.WatchFaceView
import com.artemchep.horlogo.util.TimezoneManager
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.*

/**
 * @author Artem Chepurnoy
 */
class WatchFaceService : CanvasWatchFaceService() {

    companion object {
        private const val TAG = "WatchFaceService"
    }

    override fun onCreateEngine() = WatchFaceEngine()

    /**
     * @author Artem Chepurnoy
     */
    open inner class WatchFaceEngine : CanvasWatchFaceService.Engine(), Config.OnConfigChangedListener<String> {

        private lateinit var view: WatchFaceView

        private lateinit var timeZoneManager: TimezoneManager

        private lateinit var theme: Theme

        /**
         * Ambient theme used when in ambient mode.
         */
        private val themeAmbient = Theme(
                backgroundColor = Color.BLACK,
                clockHourColor = Color.WHITE,
                clockMinuteColor = Color.GRAY,
                complicationColor = Color.LTGRAY
        )

        private var configRegistration: Config.Registration<String>? = null

        private val calendar = Calendar.getInstance()

        /** Surface width */
        private var width = 0
        /** Surface height */
        private var height = 0

        /** Maps complication ids to corresponding complications data */
        private val complicationDataSparse = SparseArray<Complication>()

        private lateinit var iconLoaderDispatcher: ThreadPoolDispatcher

        override fun onCreate(holder: SurfaceHolder?) {
            super.onCreate(holder)
            val context = applicationContext
            timeZoneManager = TimezoneManager(context)
            iconLoaderDispatcher = newSingleThreadContext(TAG)

            setActiveComplications(*WATCH_COMPLICATIONS)
            setWatchFaceStyle(WatchFaceStyle.Builder(this@WatchFaceService)
                    .setAcceptsTapEvents(true)
                    .build())

            val layoutName = Cfg.layoutName
            val layoutRes = when (layoutName) {
                Cfg.LAYOUT_HORIZONTAL -> R.layout.watch_face_horizontal
                else -> R.layout.watch_face
            }

            view = LayoutInflater
                    .from(this@WatchFaceService)
                    .inflate(layoutRes, null, false)
                    .let { it as WatchFaceView }
                    .apply {
                        isDrawingCacheEnabled = false
                        // Set the layout name as a tag
                        tag = layoutName
                    }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            // Unregister previous config listener,
            // if registered.
            configRegistration?.unregister()

            if (visible) {
                configRegistration = Cfg.addListener(this)
                timeZoneManager.start {
                    calendar.timeZone = TimeZone.getDefault()
                    onTimeTick()
                }

                // Load the config and
                // update the time

                if (view.tag as String? != Cfg.layoutName) {
                    // Update the theme model and
                    // reload the whole view
                    loadThemeFromConfig()
                    updateLayoutFromConfig()
                } else {
                    // Reload only a theme and an
                    // accent color
                    updateThemeFromConfig()
                }

                onTimeTick()
            } else {
                timeZoneManager.stop()
            }
        }

        override fun onConfigChanged(keys: Set<String>) {
            keys.forEach { key ->
                when (key) {
                    Cfg.KEY_LAYOUT -> updateLayoutFromConfig()
                    Cfg.KEY_THEME -> updateThemeFromConfig()
                    Cfg.KEY_ACCENT_COLOR -> updateThemeAccentColorFromConfig()
                    else -> return
                }
            }

            // Request a redraw
            invalidate()
        }

        private fun updateLayoutFromConfig() {
            val layoutName = Cfg.layoutName
            val layoutRes = when (layoutName) {
                Cfg.LAYOUT_HORIZONTAL -> R.layout.watch_face_horizontal
                else -> R.layout.watch_face
            }

            view = LayoutInflater
                    .from(this@WatchFaceService)
                    .inflate(layoutRes, null, false)
                    .let { it as WatchFaceView }
                    .apply {
                        isDrawingCacheEnabled = false
                        setAntiAlias(!isInAmbientMode)
                        setTime(calendar)

                        // Set complications
                        complicationDataSparse.forEach { key, value ->
                            val text = value.run { longMsg ?: shortMsg }
                            val icon = value.run {
                                // Return the ambient icon if not null & in ambient mode, or
                                // normal one.
                                ambientIconDrawable
                                        ?.takeIf { isInAmbientMode }
                                        ?: normalIconDrawable
                            }

                            setComplicationContentText(key, text)
                            setComplicationIcon(key, icon)
                        }

                        // Set the layout name as a tag
                        tag = layoutName
                    }

            bindTheme()
        }

        private fun updateThemeFromConfig() {
            // Load the theme from config and apply
            // the accent color.
            loadThemeFromConfig()

            // Bind to view
            bindTheme()
        }

        private fun loadThemeFromConfig() {
            theme = when (Cfg.themeName) {
                Cfg.THEME_BLACK -> Theme.BLACK
                Cfg.THEME_DARK -> Theme.DARK
                Cfg.THEME_LIGHT -> Theme.LIGHT
                else -> throw IllegalArgumentException()
            }.copy(clockHourColor = Cfg.accentColor)
        }

        private fun updateThemeAccentColorFromConfig() {
            // Apply the accent color.
            theme.clockHourColor = Cfg.accentColor

            // Bind to view
            bindTheme()
        }

        override fun onTimeTick() {
            super.onTimeTick()
            val now = System.currentTimeMillis()
            calendar.timeInMillis = now

            view.setTime(calendar)

            complicationDataSparse.forEach { id, value ->
                if (value.raw.isTimeDependent) {
                    value.refreshMessage(this@WatchFaceService)
                    value.refreshActive()

                    bindComplicationContentText(id, value)
                }
            }

            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            view.setAntiAlias(!inAmbientMode)

            // Bind every complication icon, to apply the ambient mode icons
            // if needed.
            complicationDataSparse.forEach { id, complication ->
                bindComplicationIcon(id, complication)
            }

            // Update the theme
            bindTheme()

            // Post redraw on next
            // frame
            invalidate()
        }

        override fun onComplicationDataUpdate(watchFaceComplicationId: Int, data: ComplicationData?) {
            super.onComplicationDataUpdate(watchFaceComplicationId, data)
            if (data == null
                    || (data.shortText == null && data.shortTitle == null
                            && data.longText == null && data.longTitle == null)) {
                complicationDataSparse.remove(watchFaceComplicationId)
                view.apply {
                    setComplicationIcon(watchFaceComplicationId, null)
                    setComplicationContentText(watchFaceComplicationId, null)
                }
            } else {
                val complication = Complication(raw = data).apply {
                    refreshMessage(this@WatchFaceService)
                    refreshActive()
                }

                launch(iconLoaderDispatcher) {
                    val normalIcon = data.icon?.loadDrawable(this@WatchFaceService)
                    val ambientIcon = data.burnInProtectionIcon?.loadDrawable(this@WatchFaceService)

                    launch(UI) {
                        complication.normalIconDrawable = normalIcon
                        complication.ambientIconDrawable = ambientIcon

                        // Set the icon
                        bindComplicationIcon(watchFaceComplicationId, complication)
                        invalidate()
                    }
                }

                complicationDataSparse.put(watchFaceComplicationId, complication)
                view.apply {
                    setComplicationIcon(watchFaceComplicationId, null)
                    bindComplicationContentText(watchFaceComplicationId, complication)
                }
            }

            // Post redraw on next
            // frame
            invalidate()
        }

        /**
         * Binds complication icon to the [view].
         * @see bindComplicationContentText
         */
        private fun bindComplicationIcon(id: Int, complication: Complication?) {
            val icon = complication?.run {
                // Return the ambient icon if not null & in ambient mode, or
                // normal one.
                ambientIconDrawable
                        ?.takeIf { isInAmbientMode }
                        ?: normalIconDrawable
            }

            view.setComplicationIcon(id, icon)
        }

        /**
         * Binds complication content text to the [view].
         * @see bindComplicationIcon
         */
        private fun bindComplicationContentText(id: Int, complication: Complication?) {
            val text = complication?.run {
                return@run if (isActive) {
                    longMsg ?: shortMsg
                } else null
            }

            view.setComplicationContentText(id, text)
        }

        /**
         * Binds [theme] (or [themeAmbient] if we are in the ambient mode)
         * to current view.
         */
        private fun bindTheme() {
            view.setTheme(if (isInAmbientMode) themeAmbient else theme)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            this.width = width
            this.height = height
            performViewLayout()
        }

        /**
         * Calls [view]'s [measure][View.measure] and [layout][View.layout] methods,
         * to position its children in place.
         */
        private fun performViewLayout() {
            val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

            view.apply {
                measure(measuredWidth, measuredHeight)
                layout(0, 0, this.measuredWidth, this.measuredHeight)
            }
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            super.onDraw(canvas, bounds)
            view.apply {
                if (isLayoutRequested) performViewLayout()
                draw(canvas)
            }
        }

        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            super.onTapCommand(tapType, x, y, eventTime)
            if (tapType != WatchFaceService.TAP_TYPE_TAP) {
                return
            }

            val target = view.findViewByLocation(x, y)
            target?.tag?.let { it as? Int }?.also { id ->
                complicationDataSparse[id]?.raw?.tapAction?.send()
            }
        }

        override fun onDestroy() {
            timeZoneManager.stop()
            configRegistration?.unregister()
            iconLoaderDispatcher.close()
            super.onDestroy()
        }

    }

}
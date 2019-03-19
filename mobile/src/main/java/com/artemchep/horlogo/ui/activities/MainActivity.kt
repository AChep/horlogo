package com.artemchep.horlogo.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.graphics.luminance
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.config.Config
import com.artemchep.horlogo.Cfg
import com.artemchep.horlogo.R
import com.artemchep.horlogo.WATCH_COMPLICATION_FIRST
import com.artemchep.horlogo.WATCH_COMPLICATION_SECOND
import com.artemchep.horlogo.contracts.IMainPresenter
import com.artemchep.horlogo.contracts.IMainView
import com.artemchep.horlogo.presenters.MainPresenterMobile
import com.artemchep.horlogo.sync.CfgDataClientAdapter
import com.artemchep.horlogo.sync.DataClientCfgAdapter
import com.artemchep.horlogo.ui.activities.base.ActivityBase
import com.artemchep.horlogo.ui.adapters.MainAdapter
import com.artemchep.horlogo.ui.dialogs.AboutDialog
import com.artemchep.horlogo.ui.dialogs.PickerDialog
import com.artemchep.horlogo.ui.drawables.CircleDrawable
import com.artemchep.horlogo.ui.interfaces.OnItemClickListener
import com.artemchep.horlogo.ui.model.ConfigItem
import com.artemchep.horlogo.ui.model.ConfigPickerItem
import com.artemchep.horlogo.ui.model.Theme
import com.artemchep.horlogo.ui.views.WatchFaceView
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.*

/**
 * @author Artem Chepurnoy
 */
class MainActivity : ActivityBase<IMainView, IMainPresenter>(), IMainView,
    Config.OnConfigChangedListener<String>,
    PickerDialog.PickerDialogCallback,
    OnItemClickListener<ConfigItem>,
    View.OnClickListener {

    override val view: IMainView = this

    private lateinit var dataClient: DataClient

    private lateinit var adapter: MainAdapter

    private lateinit var watchFaceView: WatchFaceView

    private val dataSyncObserver = DataClientCfgAdapter(this)
    private val cfgSyncObserver = CfgDataClientAdapter(this)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_TIME_TICK,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED -> updateWatchFaceTime()
            }
        }
    }

    override fun createPresenter(): IMainPresenter = MainPresenterMobile(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionAboutBtn.setOnClickListener(this)

        adapter = MainAdapter(presenter.items).apply {
            // Handle item click
            onItemClickListener = this@MainActivity
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        dataClient = Wearable.getDataClient(this)
    }

    override fun onStart() {
        super.onStart()
        Cfg.observe(this)
        dataClient.addListener(dataSyncObserver)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIME_TICK)
        }
        registerReceiver(broadcastReceiver, filter)

        updateWatchFaceLayout()
        updateAppAccentColor()
    }

    override fun onStop() {
        unregisterReceiver(broadcastReceiver)
        dataClient.removeListener(dataSyncObserver)
        Cfg.removeObserver(this)
        super.onStop()
    }

    override fun onConfigChanged(keys: Set<String>) {
        keys.forEach { key ->
            when (key) {
                Cfg.KEY_THEME -> updateWatchFaceTheme()
                Cfg.KEY_ACCENT_COLOR -> {
                    updateWatchFaceTheme()
                    updateAppAccentColor()
                }
                Cfg.KEY_LAYOUT -> updateWatchFaceLayout()
            }
        }

        cfgSyncObserver.onConfigChanged(keys)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.actionAboutBtn -> presenter.navigateTo(IMainPresenter.ITEM_ABOUT)
        }
    }

    override fun onItemClick(view: View, model: ConfigItem) {
        presenter.navigateTo(model.id)
    }

    override fun onSingleItemPicked(requestCode: Int, resultCode: Int, key: String?) {
        presenter.result(requestCode, resultCode, key)
    }

    //
    // WATCH FACE
    //

    private fun updateWatchFaceLayout() {
        val layoutName = Cfg.layoutName
        val layoutRes = when (layoutName) {
            Cfg.LAYOUT_HORIZONTAL -> R.layout.watch_face_horizontal
            else -> R.layout.watch_face
        }

        watchFaceView = LayoutInflater
            .from(this)
            .inflate(layoutRes, null, false)
            .let { it as WatchFaceView }
            .apply {
                isDrawingCacheEnabled = true
                setAntiAlias(true)

                // Set complications
                val weatherIcon = getDrawable(R.drawable.ic_weather_partly_cloudy)
                setComplicationContentText(WATCH_COMPLICATION_FIRST, "Partly cloudy")
                setComplicationIcon(WATCH_COMPLICATION_FIRST, weatherIcon)

                val batteryIcon = getDrawable(R.drawable.ic_battery)
                setComplicationContentText(WATCH_COMPLICATION_SECOND, "100%")
                setComplicationIcon(WATCH_COMPLICATION_SECOND, batteryIcon)
            }

        updateWatchFaceTheme()
        updateWatchFaceTime()

        watchFaceViewContainer.apply {
            removeAllViews()
            addView(watchFaceView)
        }
    }

    private fun updateWatchFaceTheme() {
        val theme = when (Cfg.themeName) {
            Cfg.THEME_BLACK -> Theme.BLACK
            Cfg.THEME_DARK -> Theme.DARK
            Cfg.THEME_LIGHT -> Theme.LIGHT
            else -> throw IllegalArgumentException()
        }.copy(clockHourColor = Cfg.accentColor)

        watchFaceView.setTheme(theme)
        watchFaceView.background = CircleDrawable(theme.backgroundColor)
    }

    private fun updateWatchFaceTime() {
        val calendar = Calendar.getInstance()
        watchFaceView.setTime(calendar)
    }

    private fun updateAppAccentColor() {
        val accentColor = Cfg.accentColor
        val isContentColorDark = accentColor.luminance < 0.5
        val contentColor = if (isContentColorDark) {
            Color.WHITE
        } else Color.BLACK

        // Change the color of toolbar
        appbarView.setBackgroundColor(accentColor)
        titleTextView.setTextColor(contentColor)
        actionAboutBtn.imageTintList = ColorStateList.valueOf(contentColor)

        // Change the color of status bar
        window.apply {
            statusBarColor = accentColor
            val visibility = if (isContentColorDark) {
                decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = visibility
        }
    }

    //
    // MAIN
    //

    override fun notifyDataChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun notifyItemChanged(position: Int) {
        adapter.tellItemChanged(position)
    }

    override fun showPickerScreenForResult(
        title: String?,
        key: String,
        items: List<ConfigPickerItem>,
        resultCode: Int
    ) {
        val dialog = PickerDialog.create(resultCode, key, title, ArrayList(items))
        dialog.show(supportFragmentManager, PickerDialog.TAG)
    }

    override fun showComplicationsScreen() {
        error("Mobile devices can not have complications screen")
    }

    override fun showAboutScreen() {
        val dialog = AboutDialog()
        dialog.show(supportFragmentManager, AboutDialog.TAG)
    }

}

package com.artemchep.horlogo

import android.app.Application
import com.artemchep.horlogo.sync.CfgDataClientAdapter

/**
 * @author Artem Chepurnoy
 */
class Heart : Application() {

    private val cfgSyncObserver = CfgDataClientAdapter(this)

    override fun onCreate() {
        super.onCreate()
        Cfg.init(this)
        Cfg.observe(cfgSyncObserver)
    }

}
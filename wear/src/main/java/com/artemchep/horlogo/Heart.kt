package com.artemchep.horlogo

import android.app.Application

/**
 * @author Artem Chepurnoy
 */
class Heart : Application() {

    override fun onCreate() {
        super.onCreate()
        Config.init(this)
    }

}
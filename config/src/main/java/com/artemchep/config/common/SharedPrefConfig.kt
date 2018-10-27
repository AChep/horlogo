package com.artemchep.config.common

import android.content.Context
import com.artemchep.config.Config
import com.artemchep.config.editor.common.SharedPrefEditor
import com.artemchep.config.extensions.asStore

/**
 * @author Artem Chepurnoy
 */
open class SharedPrefConfig(private val sharedPrefName: String) : Config<String>() {

    /**
     * Initializes the config with values loaded
     * from [shared preference file][sharedPrefName].
     */
    fun init(context: Context) {
        val store = context.getSharedPreferences(sharedPrefName, 0).asStore()
        init(store)
    }

    fun edit(context: Context, block: () -> Unit) {
        val editor = SharedPrefEditor(context, sharedPrefName)
        performEdit(editor, block)
    }

}
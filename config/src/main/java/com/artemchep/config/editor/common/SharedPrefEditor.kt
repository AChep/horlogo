package com.artemchep.config.editor.common

import android.annotation.SuppressLint
import android.content.Context
import com.artemchep.config.editor.Editor

/**
 * Editor that stores configuration values in
 * shared preferences.
 *
 * @author Artem Chepurnoy
 */
class SharedPrefEditor(context: Context, name: String) : Editor<String> {

    @SuppressLint("CommitPrefEdits")
    private val editorDelegate = lazy { context.getSharedPreferences(name, 0).edit() }

    private val editor by editorDelegate

    /**
     * Applies the changes and writes them
     * to the shared preferences.
     */
    override fun apply() {
        // Do not apply if no changes
        // were made.
        if (editorDelegate.isInitialized())
            editor.apply()
    }

    override fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
    }

    override fun putString(key: String, value: String) {
        editor.putString(key, value)
    }

    override fun putLong(key: String, value: Long) {
        editor.putLong(key, value)
    }

    override fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
    }

}
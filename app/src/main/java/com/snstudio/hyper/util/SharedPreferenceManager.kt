package com.snstudio.hyper.util

import android.content.Context
import com.snstudio.hyper.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class PrefsTag(val tag: String) {
    PERMISSION_NOTIFICATION("PERMISSION_NOTIFICATION"),
}

@Singleton
class SharedPreferenceManager
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs =
            context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

        fun putBoolean(
            prefsTag: PrefsTag,
            data: Boolean,
        ) = prefs.edit().putBoolean(prefsTag.tag, data).apply()

        fun getBoolean(prefsTag: PrefsTag): Boolean = prefs.getBoolean(prefsTag.tag, false)

        fun putString(
            prefsTag: PrefsTag,
            data: String,
        ) = prefs.edit().putString(prefsTag.tag, data).apply()

        fun getString(prefsTag: PrefsTag): String? = prefs.getString(prefsTag.tag, null)

        fun putStringArray(
            prefsTag: PrefsTag,
            data: MutableSet<String>,
        ) = prefs.edit().putStringSet(prefsTag.tag, data).apply()

        fun getStringArray(prefsTag: PrefsTag): MutableSet<String>? = prefs.getStringSet(prefsTag.tag, null)
    }

package com.vanphong.foodnfit.util

import android.content.Context

object LanguagePreferenceHelper {
    private const val PREF_NAME = "language_pref"
    private const val KEY_LANGUAGE = "app_language"

    fun setLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en" // "en" mặc định
    }
}

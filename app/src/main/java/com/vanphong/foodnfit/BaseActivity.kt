package com.vanphong.foodnfit

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanphong.foodnfit.util.LanguagePreferenceHelper
import com.vanphong.foodnfit.util.LanguageUtils

abstract class BaseActivity: AppCompatActivity(){
    override fun attachBaseContext(newBase: Context) {
        val language = LanguagePreferenceHelper.getLanguage(newBase)
        val context = LanguageUtils.setLocale(newBase, language)
        super.attachBaseContext(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val languageCode = LanguagePreferenceHelper.getLanguage(this)
        LanguageUtils.setLocale(this, languageCode) // cần thiết nếu bạn dùng context trực tiếp
        super.onCreate(savedInstanceState)
    }
}
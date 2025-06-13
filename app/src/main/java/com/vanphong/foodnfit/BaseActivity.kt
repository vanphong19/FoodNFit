package com.vanphong.foodnfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.vanphong.foodnfit.activity.SignInActivity
import com.vanphong.foodnfit.util.LanguagePreferenceHelper
import com.vanphong.foodnfit.util.LanguageUtils
import com.vanphong.foodnfit.viewModel.AuthViewModel

abstract class BaseActivity: AppCompatActivity(){
    protected lateinit var authViewModel: AuthViewModel
    override fun attachBaseContext(newBase: Context) {
        val language = LanguagePreferenceHelper.getLanguage(newBase)
        val context = LanguageUtils.setLocale(newBase, language)
        super.attachBaseContext(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val languageCode = LanguagePreferenceHelper.getLanguage(this)
        LanguageUtils.setLocale(this, languageCode) // cần thiết nếu bạn dùng context trực tiếp
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        authViewModel.logout.observe(this) { isLogout ->
            if (isLogout == true) {
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

}
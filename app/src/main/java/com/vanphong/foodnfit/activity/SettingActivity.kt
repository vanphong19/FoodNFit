package com.vanphong.foodnfit.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.radiobutton.MaterialRadioButton
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivitySettingBinding
import com.vanphong.foodnfit.util.LanguagePreferenceHelper

class SettingActivity : BaseActivity() {
    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentLang = LanguagePreferenceHelper.getLanguage(this)

        binding.textviewSubLanguage.text = when (currentLang) {
            "vi" -> getString(R.string.vietnamese)
            "en" -> getString(R.string.english)
            else -> getString(R.string.english) // fallback
        }
    }

    private fun showLanguageDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_language_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window ?: return
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.attributes.gravity = Gravity.CENTER

        val btnVietNam = dialogView.findViewById<MaterialRadioButton>(R.id.btn_vietnam)
        val btnEnglish = dialogView.findViewById<MaterialRadioButton>(R.id.btn_english)

        val currentLang = LanguagePreferenceHelper.getLanguage(this)

        // Check RadioButton tương ứng
        when (currentLang) {
            "vi" -> {
                btnVietNam.isChecked = true
            }
            "en" -> {
                btnEnglish.isChecked = true
            }
        }

        btnVietNam.setOnClickListener {
            if (currentLang != "vi") {
                LanguagePreferenceHelper.setLanguage(this, "vi")
                restartApp() // hoặc restartApp()
            }
            dialog.dismiss()
        }

        btnEnglish.setOnClickListener {
            if (currentLang != "en") {
                LanguagePreferenceHelper.setLanguage(this, "en")
                restartApp()
            }
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
    }


    fun onLayoutLanguageClick(view: View) {
        showLanguageDialog()
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
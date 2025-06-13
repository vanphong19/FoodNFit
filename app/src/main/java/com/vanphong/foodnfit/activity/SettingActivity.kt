package com.vanphong.foodnfit.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.radiobutton.MaterialRadioButton
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivitySettingBinding
import com.vanphong.foodnfit.util.LanguagePreferenceHelper
import com.vanphong.foodnfit.util.NotificationUtils

class SettingActivity : BaseActivity() {
    private var _binding: ActivitySettingBinding? = null
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private val PERMISSION_REQUEST_CODE = 1002
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

        checkNotificationPermission()

        setupNotificationSwitch()
        setupStepPermissionSwitch()
    }
    private fun setupNotificationSwitch() {
        binding.switchNotification.setOnCheckedChangeListener{_, isChecked ->
            if(isChecked){
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                    requestNotificationPermission()
                }
                else{
                    showNotification()
                }
            }
            else{
                showConfirmationDialog()
            }
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




    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            binding.switchNotification.isChecked = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification()
            } else {
                Toast.makeText(this, getString(R.string.permission_deny), Toast.LENGTH_SHORT).show()
                binding.switchNotification.isChecked = false
                showPermissionDeniedDialog()
            }
        }else if (requestCode == PERMISSION_REQUEST_CODE) { // 🆕 quyền bước chân
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            binding.switchStepPermission.setOnCheckedChangeListener(null)
            binding.switchStepPermission.isChecked = granted

            if (granted) {
                Toast.makeText(this, getString(R.string.step_permission_granted), Toast.LENGTH_SHORT).show()
            } else {
                showStepPermissionDeniedDialog()
            }
            setupStepPermissionSwitch()
        }
    }
    private fun cancelNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun showNotification(){
        NotificationUtils.showNotification(this, "Reminder", "Bạn có 1 nhắc nhở mới")
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showPermissionDeniedDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_denied_title))  // Lấy tiêu đề từ strings.xml
            .setMessage(getString(R.string.permission_denied_message))  // Lấy thông báo từ strings.xml
            .setPositiveButton(getString(R.string.open_settings_button)) { _, _ ->
                openAppSettings()  // Mở Cài đặt ứng dụng khi người dùng nhấn đồng ý
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                dialog.dismiss()  // Đóng dialog khi người dùng nhấn hủy
            }
            .create()

        dialog.show()  // Hiển thị dialog
    }

    private fun showConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_disable_title))  // Lấy tiêu đề từ strings.xml
            .setMessage(getString(R.string.confirm_disable_message))  // Lấy thông báo từ strings.xml
            .setPositiveButton(getString(R.string.open_settings_button)) { _, _ ->
                // Nếu chọn OK, mở Cài đặt ứng dụng để tắt quyền thông báo
                openAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ ->
                // Nếu chọn Cancel, không làm gì, giữ switch ở trạng thái ON
                binding.switchNotification.isChecked = true  // Giữ switch bật
                dialog.dismiss()  // Đóng dialog
            }
            .create()

        dialog.show()  // Hiển thị dialog
    }
    // 🆕 Kiểm tra trạng thái quyền bước chân & gán vào switch
    private fun setupStepPermissionSwitch() {
        binding.switchStepPermission.isChecked = isStepPermissionGranted()

        binding.switchStepPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!isStepPermissionGranted()) {
                    requestStepPermission()
                }
            } else {
                Toast.makeText(this, getString(R.string.step_permission_disabled), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🆕 Kiểm tra quyền bước chân
    private fun isStepPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    // 🆕 Xin quyền bước chân
    private fun requestStepPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // 🆕 Hiện dialog khi người dùng từ chối quyền bước chân
    private fun showStepPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.step_permission_denied_title))
            .setMessage(getString(R.string.step_permission_denied_message))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(getString(R.string.ignore), null)
            .show()
    }
    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }
}
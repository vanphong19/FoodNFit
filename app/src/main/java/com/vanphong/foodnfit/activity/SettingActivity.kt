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
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.radiobutton.MaterialRadioButton
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivitySettingBinding
import com.vanphong.foodnfit.repository.StepsTrackingRepository
import com.vanphong.foodnfit.util.LanguagePreferenceHelper
import com.vanphong.foodnfit.util.NotificationUtils
import com.vanphong.foodnfit.util.StepsPermissionHelper
import com.vanphong.foodnfit.util.StepsTrackingManager

class SettingActivity : BaseActivity() {
    private var _binding: ActivitySettingBinding? = null
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var stepsTrackingManager: StepsTrackingManager
    private lateinit var permissionHelper: StepsPermissionHelper
    private lateinit var switchStepsTracking: MaterialSwitch
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

        binding.layoutChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        checkNotificationPermission()

        setupNotificationSwitch()
        switchStepsTracking = binding.switchStepPermission // Đảm bảo ID này có trong file layout của bạn
        stepsTrackingManager = StepsTrackingManager(this, StepsTrackingRepository())
        setupPermissionHelper()
        setupStepsTrackingSwitchListener()
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
        }
//        else if (requestCode == PERMISSION_REQUEST_CODE) { // 🆕 quyền bước chân
//            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
//            binding.switchStepPermission.setOnCheckedChangeListener(null)
//            binding.switchStepPermission.isChecked = granted
//
//            if (granted) {
//                Toast.makeText(this, getString(R.string.step_permission_granted), Toast.LENGTH_SHORT).show()
//            } else {
//                showStepPermissionDeniedDialog()
//            }
//            setupStepPermissionSwitch()
//        }
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

    private fun setupPermissionHelper() {
        permissionHelper = StepsPermissionHelper(
            activity = this,
            onPermissionsGranted = {
                // Khi người dùng cấp quyền thành công, tiến hành bật tracking
                enableStepsTracking()
            },
            onPermissionsDenied = {
                // Nếu người dùng từ chối, đảm bảo switch ở trạng thái TẮT
                Toast.makeText(this, "Quyền bị từ chối. Không thể bật theo dõi.", Toast.LENGTH_SHORT).show()
                // Cập nhật lại UI, switch sẽ tự động tắt
                updateSwitchState()
            }
        )
    }

    /**
     * Gán listener cho switch theo dõi bước chân.
     * Logic này chỉ được kích hoạt bởi hành động của người dùng (gạt switch).
     */
    private fun setupStepsTrackingSwitchListener() {
        switchStepsTracking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Người dùng muốn BẬT
                if (stepsTrackingManager.hasAllPermissions()) {
                    enableStepsTracking()
                } else {
                    // Nếu chưa có quyền, bắt đầu quy trình xin quyền
                    permissionHelper.checkAndRequestPermissions()
                }
            } else {
                // Người dùng muốn TẮT
                disableStepsTracking()
            }
        }
    }

    /**
     * Hàm này là nguồn chân lý duy nhất cho trạng thái của switch bước chân.
     * Nó kiểm tra cả quyền và trạng thái lưu trong SharedPreferences.
     */
    private fun updateSwitchState() {
        // Điều kiện để switch được BẬT:
        // 1. App phải có TẤT CẢ quyền.
        // 2. Tính năng tracking phải đang được bật trong SharedPreferences.
        val shouldBeOn = stepsTrackingManager.hasAllPermissions() && stepsTrackingManager.isTrackingEnabled()

        // Tạm thời gỡ listener để tránh kích hoạt sự kiện một cách không cần thiết khi code tự đổi trạng thái
        switchStepsTracking.setOnCheckedChangeListener(null)
        switchStepsTracking.isChecked = shouldBeOn
        // Gắn lại listener để nhận các hành động tiếp theo của người dùng
        setupStepsTrackingSwitchListener()
    }

    private fun enableStepsTracking() {
        // Hàm này được gọi KHI người dùng đã cấp đủ quyền VÀ muốn bật
        if (stepsTrackingManager.startStepsTracking()) {
            Toast.makeText(this, "Đã bật theo dõi bước chân.", Toast.LENGTH_SHORT).show()
        } else {
            // Trường hợp hiếm gặp, ví dụ quyền bị thu hồi ngay lập tức
            Toast.makeText(this, "Không thể bắt đầu theo dõi.", Toast.LENGTH_SHORT).show()
        }
        // Cập nhật lại trạng thái UI sau khi thực hiện hành động
        updateSwitchState()
    }

    private fun disableStepsTracking() {
        stepsTrackingManager.stopStepsTracking()
        Toast.makeText(this, "Đã tắt theo dõi bước chân.", Toast.LENGTH_SHORT).show()
        // Cập nhật lại trạng thái UI sau khi thực hiện hành động
        updateSwitchState()
    }
    private fun updateAllSwitchStates() {
        // Cập nhật tất cả các switch trong màn hình
        updateSwitchState()
        checkNotificationPermission()
    }
    override fun onResume() {
        super.onResume()
        updateAllSwitchStates()
    }
}
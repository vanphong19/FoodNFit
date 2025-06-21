package com.vanphong.foodnfit

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.vanphong.foodnfit.databinding.ActivityMainBinding
import com.vanphong.foodnfit.repository.StepsTrackingRepository
import com.vanphong.foodnfit.util.StepsPermissionHelper
import com.vanphong.foodnfit.util.StepsTrackingManager

class MainActivity : BaseActivity() {

    companion object {
        private const val PERMISSION_DIALOG_DELAY = 1500L
        private const val TAG = "MainActivity"
        private const val PREF_BATTERY_OPTIMIZATION_SHOWN = "battery_optimization_dialog_shown"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var stepsTrackingManager: StepsTrackingManager
    private lateinit var permissionHelper: StepsPermissionHelper
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var powerManager: PowerManager

    private val mainHandler = Handler(Looper.getMainLooper())
    private var permissionDialogRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        initializeComponents()
        handleInitialPermissionFlow()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.fragment)
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun initializeComponents() {
        stepsTrackingManager = StepsTrackingManager(this, StepsTrackingRepository())
        sharedPrefs = getSharedPreferences(StepsTrackingManager.PREFS_NAME, Context.MODE_PRIVATE)
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        setupPermissionHelper()
    }

    private fun setupPermissionHelper() {
        permissionHelper = StepsPermissionHelper(
            activity = this,
            onPermissionsGranted = { onPermissionsGranted() },
            onPermissionsDenied = { onPermissionsDenied() }
        )
    }

    private fun handleInitialPermissionFlow() {
        val initialPromptShown = sharedPrefs.getBoolean(
            StepsTrackingManager.PREF_INITIAL_PERMISSION_PROMPT_SHOWN,
            false
        )

        when {
            stepsTrackingManager.hasAllPermissions() -> {
                Log.d(TAG, "🔍 Already have all permissions, proceeding...")
                onPermissionsGranted()
            }
            !initialPromptShown -> {
                Log.d(TAG, "🔍 Initial prompt not shown yet, scheduling dialog...")
                schedulePermissionDialog()
            }
            else -> {
                Log.d(TAG, "🔍 Permissions not granted and prompt already shown")
            }
        }
    }

    private fun schedulePermissionDialog() {
        permissionDialogRunnable = Runnable {
            if (!isFinishing && !isDestroyed) {
                showPermissionExplanationDialog()
            }
        }
        mainHandler.postDelayed(permissionDialogRunnable!!, PERMISSION_DIALOG_DELAY)
    }

    private fun showPermissionExplanationDialog() {
        if (isFinishing || isDestroyed) return

        AlertDialog.Builder(this)
            .setTitle("Theo dõi bước chân")
            .setMessage(
                "Để tự động ghi lại các lần đi bộ ngay cả khi ứng dụng chạy nền, " +
                        "FoodnFit cần các quyền sau:\n\n" +
                        "• Hoạt động thể chất: Để đếm số bước\n" +
                        "• Thông báo: Để hiển thị tiến trình\n\n" +
                        "Dữ liệu của bạn sẽ được bảo mật và chỉ lưu trên thiết bị."
            )
            .setPositiveButton("Cấp quyền") { dialog, _ ->
                dialog.dismiss()
                markInitialPromptAsShown()
                permissionHelper.checkAndRequestPermissions()
            }
            .setNegativeButton("Để sau") { dialog, _ ->
                dialog.dismiss()
                markInitialPromptAsShown()
                onPermissionsDenied()
            }
            .setCancelable(false)
            .show()
    }

    private fun markInitialPromptAsShown() {
        sharedPrefs.edit()
            .putBoolean(StepsTrackingManager.PREF_INITIAL_PERMISSION_PROMPT_SHOWN, true)
            .apply()
    }

    private fun onPermissionsGranted() {
        Log.d(TAG, "✅ All permissions granted, starting steps tracking...")

        // Log current status for debugging
        Log.d(TAG, "📊 Current tracking stats:\n${stepsTrackingManager.getTrackingStats()}")

        // Start tracking (this will set isTrackingEnabled to true)
        val success = stepsTrackingManager.startStepsTracking()

        if (success) {
            Log.i(TAG, "🎉 Steps tracking started successfully!")
            Log.d(TAG, "📊 After start - tracking enabled: ${stepsTrackingManager.isTrackingEnabled()}")

            // Check battery optimization after starting tracking
            checkAndShowBatteryOptimizationDialog()
        } else {
            Log.e(TAG, "❌ Failed to start steps tracking")
        }
    }

    /**
     * Kiểm tra xem app có được ignore battery optimization chưa
     */
    private fun isBatteryOptimizationDisabled(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(packageName)
        } else {
            true // Các phiên bản cũ hơn không có battery optimization
        }
    }

    /**
     * Kiểm tra và hiển thị dialog battery optimization nếu cần
     */
    private fun checkAndShowBatteryOptimizationDialog() {
        val batteryOptimizationShown = sharedPrefs.getBoolean(PREF_BATTERY_OPTIMIZATION_SHOWN, false)

        Log.d(TAG, "🔋 Battery optimization disabled: ${isBatteryOptimizationDisabled()}")
        Log.d(TAG, "🔋 Battery dialog shown before: $batteryOptimizationShown")

        // Chỉ hiển thị dialog nếu:
        // 1. Battery optimization chưa được tắt
        // 2. Dialog chưa được hiển thị trước đó
        if (!isBatteryOptimizationDisabled() && !batteryOptimizationShown) {
            mainHandler.postDelayed({
                showBatteryOptimizationDialog()
            }, 2000L)
        } else {
            Log.d(TAG, "🔋 Skipping battery optimization dialog")
        }
    }

    private fun showBatteryOptimizationDialog() {
        if (isFinishing || isDestroyed) return

        // Đánh dấu đã hiển thị dialog
        sharedPrefs.edit()
            .putBoolean(PREF_BATTERY_OPTIMIZATION_SHOWN, true)
            .apply()

        AlertDialog.Builder(this)
            .setTitle("Tối ưu pin")
            .setMessage(
                "Để theo dõi bước chân chính xác khi chạy nền, hãy:\n\n" +
                        "• Tắt tối ưu pin cho FoodnFit\n" +
                        "• Cho phép ứng dụng chạy nền\n\n" +
                        "Điều này giúp ứng dụng không bị hệ thống tắt."
            )
            .setPositiveButton("Cài đặt pin") { _, _ ->
                openBatteryOptimizationSettings()
            }
            .setNegativeButton("Bỏ qua") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openBatteryOptimizationSettings() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open battery optimization settings", e)
            permissionHelper.openAppSettings()
        }
    }

    private fun onPermissionsDenied() {
        Log.w(TAG, "❌ Permissions denied. Background tracking will not work.")
        showPermissionDeniedDialog()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Quyền bị từ chối")
            .setMessage(
                "Không thể theo dõi bước chân khi ứng dụng chạy nền.\n\n" +
                        "Bạn có thể cấp quyền lại trong Cài đặt > Ứng dụng > FoodnFit > Quyền"
            )
            .setPositiveButton("Đến Cài đặt") { _, _ ->
                permissionHelper.openAppSettings()
            }
            .setNegativeButton("Đóng") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "📱 onResume - checking permissions...")

        // Check permissions when user returns to app
        if (stepsTrackingManager.hasAllPermissions()) {
            Log.d(TAG, "📱 onResume - permissions OK")

            // Only start if not already tracking
            if (!stepsTrackingManager.isTrackingEnabled()) {
                Log.d(TAG, "📱 onResume - tracking not enabled yet, starting...")
                onPermissionsGranted()
            } else {
                Log.d(TAG, "📱 onResume - tracking already enabled")

                // Kiểm tra lại battery optimization khi user quay lại app
                // (có thể user vừa thay đổi cài đặt)
                if (!isBatteryOptimizationDisabled()) {
                    Log.d(TAG, "📱 onResume - battery optimization still enabled")
                } else {
                    Log.d(TAG, "📱 onResume - battery optimization disabled ✅")
                }
            }
        } else {
            Log.d(TAG, "📱 onResume - permissions missing")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🔄 onDestroy - cleaning up...")

        // Cleanup to avoid memory leak
        permissionDialogRunnable?.let { runnable ->
            mainHandler.removeCallbacks(runnable)
        }
    }
}
package com.vanphong.foodnfit.util

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

class StepsPermissionHelper(
    private val activity: FragmentActivity,
    private val onPermissionsGranted: () -> Unit,
    private val onPermissionsDenied: () -> Unit
) {
    private val TAG = "StepsPermissionHelper"

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .build()

    private val activityRecognitionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "🔐 Activity Recognition Permission: ${if (isGranted) "GRANTED" else "DENIED"}")
        if (isGranted) {
            requestGoogleFitPermissions()
        } else {
            showPermissionDeniedDialog(isGoogleFit = false)
            onPermissionsDenied()
        }
    }

    private val googleFitLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "🔐 Google Fit Permission Result Code: ${result.resultCode}")
        if (hasGoogleFitPermission()) {
            Log.i(TAG, "✅ Tất cả quyền đã được cấp!")
            onPermissionsGranted()
        } else {
            Log.w(TAG, "❌ Google Fit permission denied")
            showPermissionDeniedDialog(isGoogleFit = true)
            onPermissionsDenied()
        }
    }

    fun checkAndRequestPermissions() {
        Log.d(TAG, "🔍 Checking permissions...")
        checkNotificationPermission()
    }

    private fun hasActivityRecognitionPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val granted = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "🔍 Activity Recognition Permission: $granted")
            granted
        } else {
            Log.d(TAG, "🔍 Activity Recognition Permission: Not required (Android < 10)")
            true
        }
    }

    private fun hasGoogleFitPermission(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        val hasPermission = account != null && GoogleSignIn.hasPermissions(account, fitnessOptions)
        Log.d(TAG, "🔍 Google Fit Permission: $hasPermission (Account: ${account?.email})")
        return hasPermission
    }

    private fun requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "📱 Launching Activity Recognition permission request...")
            activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            Log.d(TAG, "📱 Skipping Activity Recognition (Android < 10), requesting Google Fit...")
            requestGoogleFitPermissions()
        }
    }

    private fun requestGoogleFitPermissions() {
        Log.d(TAG, "🏃‍♂️ Preparing Google Fit permission request...")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .addExtension(fitnessOptions)
            .build()
        val signInIntent = GoogleSignIn.getClient(activity, gso).signInIntent
        Log.d(TAG, "🏃‍♂️ Launching Google Fit permission request...")
        googleFitLauncher.launch(signInIntent)
    }

    private fun showPermissionDeniedDialog(isGoogleFit: Boolean) {
        val title = if (isGoogleFit) "Cần quyền Google Fit" else "Cần quyền Hoạt động"
        val message = if (isGoogleFit) {
            "Ứng dụng cần kết nối với Google Fit để theo dõi bước chân. Vui lòng thử lại và chấp nhận quyền."
        } else {
            "Để theo dõi bước chân, ứng dụng cần quyền truy cập hoạt động thể chất. Bạn có thể cấp quyền trong Cài đặt."
        }
        val positiveButtonText = if (isGoogleFit) "Thử lại" else "Mở cài đặt"

        Log.w(TAG, "🚫 Showing permission denied dialog for: ${if (isGoogleFit) "Google Fit" else "Activity Recognition"}")

        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                if (isGoogleFit) {
                    requestGoogleFitPermissions()
                } else {
                    openAppSettings()
                }
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    fun openAppSettings() {
        Log.d(TAG, "⚙️ Opening app settings...")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    private val notificationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "🔔 Notification Permission: ${if (isGranted) "GRANTED" else "DENIED"}")
        // Continue with activity recognition check regardless
        checkActivityRecognitionPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "🔔 Requesting notification permission...")
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                checkActivityRecognitionPermission()
            }
        } else {
            checkActivityRecognitionPermission()
        }
    }

    private fun checkActivityRecognitionPermission() {
        when {
            !hasActivityRecognitionPermission() -> {
                Log.d(TAG, "📱 Requesting Activity Recognition permission...")
                requestActivityRecognitionPermission()
            }
            !hasGoogleFitPermission() -> {
                Log.d(TAG, "🏃‍♂️ Requesting Google Fit permissions...")
                requestGoogleFitPermissions()
            }
            else -> {
                Log.i(TAG, "✅ All permissions already granted!")
                onPermissionsGranted()
            }
        }
    }
}
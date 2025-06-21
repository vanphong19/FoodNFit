package com.vanphong.foodnfit.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.vanphong.foodnfit.model.StepsTrackingRequest
import com.vanphong.foodnfit.repository.StepsTrackingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class StepsTrackingManager(
    private val context: Context,
    private val repository: StepsTrackingRepository
) {
    private val TAG = "StepsTrackingManager"

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .build()

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "steps_tracking_prefs"
        const val PREF_TRACKING_ENABLED = "tracking_enabled"
        const val PREF_INITIAL_PERMISSION_PROMPT_SHOWN = "initial_permission_prompt_shown"
        const val PREF_LAST_BACKUP_TIME = "last_backup_time"
    }

    fun hasAllPermissions(): Boolean {
        val activityRecognition = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        val account = GoogleSignIn.getLastSignedInAccount(context)
        val googleFit = account != null && GoogleSignIn.hasPermissions(account, fitnessOptions)

        Log.d(TAG, "🔍 Permission Check - Activity: $activityRecognition, GoogleFit: $googleFit (Account: ${account?.email})")
        return activityRecognition && googleFit
    }

    fun startStepsTracking(): Boolean {
        Log.d(TAG, "🚀 Attempting to start steps tracking...")

        if (!hasAllPermissions()) {
            Log.e(TAG, "❌ Không đủ quyền để bắt đầu theo dõi.")
            return false
        }

        setTrackingEnabled(true)
        val serviceIntent = Intent(context, StepsTrackingService::class.java)
        try {
            ContextCompat.startForegroundService(context, serviceIntent)
            Log.i(TAG, "✅ Đã bắt đầu dịch vụ theo dõi bước chân.")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khi khởi động service: ${e.message}", e)
            return false
        }
    }

    fun stopStepsTracking() {
        Log.d(TAG, "🛑 Stopping steps tracking...")
        setTrackingEnabled(false)
        val serviceIntent = Intent(context, StepsTrackingService::class.java)
        context.stopService(serviceIntent)
        Log.i(TAG, "✅ Đã dừng dịch vụ theo dõi bước chân.")
    }

    /**
     * Method called by StepsTrackingService to add a walking session
     * This replaces the old saveWalkingSession method
     */
    fun addWalkingSession(startTime: Long, endTime: Long, stepCount: Int, distance: Float) {
        val startTimeFormatted = java.text.SimpleDateFormat("HH:mm:ss dd/MM").format(java.util.Date(startTime))
        val endTimeFormatted = java.text.SimpleDateFormat("HH:mm:ss dd/MM").format(java.util.Date(endTime))
        val duration = (endTime - startTime) / 1000 / 60 // minutes

        Log.d(TAG, "💾 Adding walking session: $startTimeFormatted -> $endTimeFormatted ($duration phút, $stepCount bước, ${String.format("%.2f", distance)}m)")

        saveStepsData(stepCount, startTime, endTime, distance, isWalkingSession = true)
    }

    /**
     * Legacy method for backward compatibility - now reads steps from Google Fit
     */
    fun saveWalkingSession(startTime: Long, endTime: Long) {
        val startTimeFormatted = java.text.SimpleDateFormat("HH:mm:ss dd/MM").format(java.util.Date(startTime))
        val endTimeFormatted = java.text.SimpleDateFormat("HH:mm:ss dd/MM").format(java.util.Date(endTime))
        val duration = (endTime - startTime) / 1000 / 60 // minutes

        Log.d(TAG, "💾 Đang lưu walking session (legacy): $startTimeFormatted -> $endTimeFormatted ($duration phút)")

        readStepsInPeriod(startTime, endTime) { steps ->
            if (steps > 0) {
                val distance = calculateDistance(steps)
                saveStepsData(steps, startTime, endTime, distance, isWalkingSession = true)
            } else {
                Log.w(TAG, "⚠️ Walking session không có bước chân nào ($duration phút), bỏ qua không lưu.")
            }
        }
    }

    fun performDailyBackup() {
        val now = System.currentTimeMillis()
        val lastBackupTime = sharedPrefs.getLong(PREF_LAST_BACKUP_TIME, 0L)
        val oneDayInMillis = 24 * 60 * 60 * 1000L

        if (now - lastBackupTime > oneDayInMillis) {
            Log.d(TAG, "📦 Performing daily backup...")
            val startOfDay = now - oneDayInMillis
            readStepsInPeriod(startOfDay, now) { steps ->
                if (steps > 0) {
                    val distance = calculateDistance(steps)
                    saveStepsData(steps, startOfDay, now, distance, isWalkingSession = false)
                    sharedPrefs.edit().putLong(PREF_LAST_BACKUP_TIME, now).apply()
                    Log.i(TAG, "✅ Daily backup completed: $steps steps, ${String.format("%.2f", distance)}m")
                } else {
                    Log.d(TAG, "📦 Daily backup: No steps found for backup period")
                }
            }
        } else {
            val hoursUntilNextBackup = (oneDayInMillis - (now - lastBackupTime)) / (1000 * 60 * 60)
            Log.d(TAG, "📦 Daily backup not needed yet (next backup in ${hoursUntilNextBackup}h)")
        }
    }

    private fun readStepsInPeriod(startTime: Long, endTime: Long, callback: (Int) -> Unit) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            Log.e(TAG, "❌ Không có Google account để đọc dữ liệu")
            callback(0)
            return
        }

        Log.d(TAG, "📖 Reading steps from Google Fit: ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(startTime))} -> ${java.text.SimpleDateFormat("HH:mm:ss").format(java.util.Date(endTime))}")

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.HOURS)
            .build()

        Fitness.getHistoryClient(context, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                var totalSteps = 0
                Log.d(TAG, "📊 Google Fit response: ${response.buckets.size} buckets")

                if (response.buckets.isNotEmpty()) {
                    for (bucket in response.buckets) {
                        for (dataSet in bucket.dataSets) {
                            for (dp in dataSet.dataPoints) {
                                for (field in dp.dataType.fields) {
                                    if (field.name == "steps") {
                                        val stepValue = dp.getValue(field).asInt()
                                        totalSteps += stepValue
                                        Log.d(TAG, "📊 Found $stepValue steps in bucket")
                                    }
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, "📊 Total steps read: $totalSteps")
                callback(totalSteps)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Lỗi khi đọc dữ liệu từ Google Fit History API", e)
                callback(0)
            }
    }

    private fun saveStepsData(steps: Int, startTime: Long, endTime: Long, distance: Float, isWalkingSession: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss") // ISO format
                val request = StepsTrackingRequest(
                    stepsCount = steps,
                    startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()).format(formatter),
                    endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()).format(formatter),
                    isWalkingSession = isWalkingSession,
                    distance = distance
                )

                Log.d(TAG, "🔄 Saving to API: $steps steps, ${String.format("%.2f", distance)}m (${if (isWalkingSession) "Session" else "Backup"})")

                val result = repository.saveSteps(request)
                result.fold(
                    onSuccess = { savedData ->
                        val type = if (isWalkingSession) "Walking Session" else "Daily Backup"
                        val startFormatted = java.text.SimpleDateFormat("HH:mm:ss dd/MM").format(java.util.Date(startTime))
                        val endFormatted = java.text.SimpleDateFormat("HH:mm:ss dd/MM").format(java.util.Date(endTime))
                        Log.i(TAG, "✅ [$type] Đã lưu thành công: $steps bước, ${String.format("%.2f", distance)}m ($startFormatted -> $endFormatted)")
                        Log.d(TAG, "💾 Saved data ID: ${savedData.id}")
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "❌ Lỗi khi lưu dữ liệu lên server: ${exception.message}", exception)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception trong saveStepsData: ${e.message}", e)
            }
        }
    }

    private fun calculateDistance(steps: Int): Float {
        // Average step length is approximately 0.762 meters (2.5 feet)
        // This matches the calculation in StepsTrackingService
        val averageStepLength = 0.762f // meters
        return steps * averageStepLength
    }

    fun isTrackingEnabled(): Boolean {
        val enabled = sharedPrefs.getBoolean(PREF_TRACKING_ENABLED, false)
        Log.d(TAG, "🔍 Tracking enabled: $enabled")
        return enabled
    }

    private fun setTrackingEnabled(enabled: Boolean) {
        sharedPrefs.edit().putBoolean(PREF_TRACKING_ENABLED, enabled).apply()
        Log.d(TAG, "💾 Tracking enabled set to: $enabled")
    }

    /**
     * Get current tracking statistics
     */
    fun getTrackingStats(): String {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val hasPermissions = hasAllPermissions()
        val isEnabled = isTrackingEnabled()
        val lastBackup = sharedPrefs.getLong(PREF_LAST_BACKUP_TIME, 0L)
        val lastBackupFormatted = if (lastBackup > 0) {
            java.text.SimpleDateFormat("dd/MM HH:mm").format(java.util.Date(lastBackup))
        } else {
            "Chưa có"
        }

        return "Account: ${account?.email ?: "None"}\n" +
                "Permissions: $hasPermissions\n" +
                "Enabled: $isEnabled\n" +
                "Last Backup: $lastBackupFormatted"
    }
}
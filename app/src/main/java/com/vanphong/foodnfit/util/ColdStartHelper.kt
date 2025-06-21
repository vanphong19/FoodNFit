package com.vanphong.foodnfit.util


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
object ColdStartHelper {
    private const val PREFS_NAME = "cold_start_prefs"
    private const val KEY_LAST_ACTIVITY = "last_activity"
    private const val KEY_WARM_UP_COUNT = "warm_up_count"
    private const val COLD_START_THRESHOLD = 15 * 60 * 1000L // 15 phút

    private var warmUpJob: Job? = null

    /**
     * Ghi nhận hoạt động của user
     */
    fun recordActivity(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
            .apply()
    }

    /**
     * Kiểm tra xem có khả năng server đang cold start không
     */
    fun isPotentialColdStart(context: Context): Boolean {
        val prefs = getPrefs(context)
        val lastActivity = prefs.getLong(KEY_LAST_ACTIVITY, 0)
        val timeSinceLastActivity = System.currentTimeMillis() - lastActivity

        val isColdStart = timeSinceLastActivity > COLD_START_THRESHOLD
        Log.d("ColdStartHelper", "Time since last activity: ${timeSinceLastActivity}ms, Cold start: $isColdStart")

        return isColdStart
    }

    /**
     * Thực hiện warm up server nếu cần
     */
    fun warmUpIfNeeded(context: Context, onResult: (Boolean) -> Unit = {}) {
        if (!isPotentialColdStart(context)) {
            Log.d("ColdStartHelper", "No warm up needed")
            onResult(true)
            return
        }

        // Cancel previous warm up job
        warmUpJob?.cancel()

        warmUpJob = CoroutineScope(Dispatchers.IO).launch {
            val success = performWarmUp(context)

            withContext(Dispatchers.Main) {
                if (success) {
                    recordActivity(context)
                }
                onResult(success)
            }
        }
    }

    /**
     * Thực hiện warm up với retry
     */
    private suspend fun performWarmUp(context: Context): Boolean {
        val prefs = getPrefs(context)
        val warmUpCount = prefs.getInt(KEY_WARM_UP_COUNT, 0)

        Log.i("ColdStartHelper", "Starting server warm up (attempt count: $warmUpCount)")

        for (attempt in 0 until 3) {
            try {
                delay(attempt * 1000L) // 0s, 1s, 2s delay

                val success = com.vanphong.foodnfit.network.RetrofitClient.warmUpServer()
                if (success) {
                    Log.i("ColdStartHelper", "Warm up successful on attempt ${attempt + 1}")

                    // Update stats
                    prefs.edit()
                        .putInt(KEY_WARM_UP_COUNT, warmUpCount + 1)
                        .apply()

                    return true
                }

                Log.w("ColdStartHelper", "Warm up failed on attempt ${attempt + 1}")

            } catch (e: Exception) {
                Log.e("ColdStartHelper", "Warm up attempt ${attempt + 1} failed: ${e.message}")
            }
        }

        Log.e("ColdStartHelper", "All warm up attempts failed")
        return false
    }

    /**
     * Show loading state khi warm up
     */
    fun showWarmUpProgress(context: Context, onProgress: (String) -> Unit) {
        if (!isPotentialColdStart(context)) return

        CoroutineScope(Dispatchers.Main).launch {
            onProgress("Đang khởi động server...")
            delay(3000)
            onProgress("Server đang thức dậy...")
            delay(3000)
            onProgress("Gần xong rồi...")
        }
    }

    /**
     * Get estimated warm up time
     */
    fun getEstimatedWarmUpTime(context: Context): Long {
        return if (isPotentialColdStart(context)) {
            30_000L // 30 seconds
        } else {
            2_000L  // 2 seconds
        }
    }

    /**
     * Reset warm up stats (for testing)
     */
    fun resetStats(context: Context) {
        getPrefs(context).edit().clear().apply()
        Log.d("ColdStartHelper", "Stats reset")
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
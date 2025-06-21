package com.vanphong.foodnfit.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.repository.StepsTrackingRepository
import java.util.concurrent.TimeUnit

class StepsTrackingService : Service() {
    private val TAG = "StepsTrackingService"

    private lateinit var stepsTrackingManager: StepsTrackingManager
    private var dataPointListener: OnDataPointListener? = null
    private val handler = Handler(Looper.getMainLooper())
    private var motionDetectionRunnable: Runnable? = null
    private var backupRunnable: Runnable? = null
    private var wakeLock: PowerManager.WakeLock? = null

    // Walking Session Management
    private var isCurrentlyWalking = false
    private var walkingStartTime = 0L
    private var lastActivityTime = 0L
    private var sessionStepCount = 0

    // Timing Constants
    private val MOTION_CHECK_INTERVAL = 15_000L // 15 seconds
    private val INACTIVITY_THRESHOLD = 120_000L // 2 minutes
    private val BACKUP_INTERVAL = 4 * 60 * 60 * 1000L // 4 hours
    private val MINIMUM_WALKING_DURATION = 30_000L // 30 seconds
    private val MINIMUM_STEPS_FOR_SESSION = 20 // Minimum steps to consider it a valid session

    companion object {
        const val NOTIFICATION_ID = 12345
        const val CHANNEL_ID = "steps_tracking_channel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "🚀 StepsTrackingService creating...")

        stepsTrackingManager = StepsTrackingManager(this, StepsTrackingRepository())
        createNotificationChannel()
        acquireWakeLock()

        if (stepsTrackingManager.hasAllPermissions()) {
            startSensorListening()
            startMotionDetection()
            startPeriodicBackup()
            Log.i(TAG, "✅ StepsTrackingService created successfully with all permissions")
        } else {
            Log.e(TAG, "❌ StepsTrackingService created but missing permissions")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "🏃‍♂️ StepsTrackingService started")
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "🛑 StepsTrackingService destroying...")

        if (isCurrentlyWalking) {
            Log.i(TAG, "⏹️ Finalizing current walking session before service destruction")
            stopWalking(System.currentTimeMillis())
        }

        stopSensorListening()
        stopMotionDetection()
        stopPeriodicBackup()
        releaseWakeLock()

        Log.i(TAG, "✅ StepsTrackingService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "FoodnFit:StepsTrackingWakeLock"
            )?.apply {
                acquire(10 * 60 * 1000L) // 10 minutes
            }
            Log.d(TAG, "🔋 WakeLock acquired")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to acquire WakeLock: ${e.message}")
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    Log.d(TAG, "🔋 WakeLock released")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to release WakeLock: ${e.message}")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Theo dõi bước chân",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Theo dõi bước chân chạy nền"
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            Log.d(TAG, "📢 Notification channel created")
        }
    }

    private fun createNotification(): Notification {
        val statusText = if (isCurrentlyWalking) {
            "Đang đi bộ... ($sessionStepCount bước)"
        } else {
            "Đang theo dõi hoạt động"
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FoodnFit - Theo dõi bước chân")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_notification) // Make sure this icon exists
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun updateNotification() {
        try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update notification: ${e.message}")
        }
    }

    private fun startSensorListening() {
        if (!stepsTrackingManager.hasAllPermissions()) {
            Log.e(TAG, "❌ Cannot start sensor listening - missing permissions")
            return
        }

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            Log.e(TAG, "❌ Cannot start sensor listening - no Google account")
            return
        }

        Log.d(TAG, "🎯 Starting sensor listening with account: ${account.email}")

        dataPointListener = OnDataPointListener { dataPoint ->
            handleDataPoint(dataPoint)
        }

        val request = SensorRequest.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setSamplingRate(5, TimeUnit.SECONDS)
            .setAccuracyMode(SensorRequest.ACCURACY_MODE_DEFAULT)
            .build()

        Fitness.getSensorsClient(this, account)
            .add(request, dataPointListener!!)
            .addOnSuccessListener {
                Log.i(TAG, "✅ Sensor listening started successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to start sensor listening: ${e.message}")
            }
    }

    private fun handleDataPoint(dataPoint: DataPoint) {
        try {
            val field = dataPoint.dataType.fields.firstOrNull() ?: return
            val steps = dataPoint.getValue(field).asInt()
            val timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS)

            if (steps > 0) {
                sessionStepCount += steps
                Log.d(TAG, "👟 Steps detected: +$steps (Total session: $sessionStepCount)")
                handleUserIsMoving(timestamp)
                updateNotification()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling data point: ${e.message}")
        }
    }

    private fun handleUserIsMoving(timestamp: Long) {
        lastActivityTime = timestamp

        if (!isCurrentlyWalking) {
            startWalking(timestamp)
        }
    }

    private fun startMotionDetection() {
        Log.d(TAG, "🔄 Starting motion detection loop")
        motionDetectionRunnable = object : Runnable {
            override fun run() {
                checkMotionStatus()
                handler.postDelayed(this, MOTION_CHECK_INTERVAL)
            }
        }
        handler.post(motionDetectionRunnable!!)
    }

    private fun stopMotionDetection() {
        motionDetectionRunnable?.let {
            handler.removeCallbacks(it)
            Log.d(TAG, "⏹️ Motion detection stopped")
        }
    }

    private fun startPeriodicBackup() {
        Log.d(TAG, "💾 Starting periodic backup")
        backupRunnable = object : Runnable {
            override fun run() {
                stepsTrackingManager.performDailyBackup()
                handler.postDelayed(this, BACKUP_INTERVAL)
            }
        }
        handler.postDelayed(backupRunnable!!, BACKUP_INTERVAL)
    }

    private fun stopPeriodicBackup() {
        backupRunnable?.let {
            handler.removeCallbacks(it)
            Log.d(TAG, "⏹️ Periodic backup stopped")
        }
    }

    private fun checkMotionStatus() {
        val currentTime = System.currentTimeMillis()

        if (isCurrentlyWalking) {
            val timeSinceLastActivity = currentTime - lastActivityTime

            if (timeSinceLastActivity > INACTIVITY_THRESHOLD) {
                Log.d(TAG, "🛑 User inactive for ${timeSinceLastActivity / 1000}s - stopping walking session")
                stopWalking(currentTime)
            }
        }

        // Log current status
        Log.v(TAG, "Motion check - Walking: $isCurrentlyWalking, Session steps: $sessionStepCount")
    }

    private fun startWalking(timestamp: Long) {
        if (isCurrentlyWalking) return

        isCurrentlyWalking = true
        walkingStartTime = timestamp
        sessionStepCount = 0
        lastActivityTime = timestamp

        Log.i(TAG, "🚶‍♂️ Walking session started at ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(timestamp))}")
        updateNotification()
    }

    private fun stopWalking(timestamp: Long) {
        if (!isCurrentlyWalking) return

        val walkingDuration = timestamp - walkingStartTime
        val isValidSession = walkingDuration >= MINIMUM_WALKING_DURATION &&
                sessionStepCount >= MINIMUM_STEPS_FOR_SESSION

        Log.i(TAG, "🏁 Walking session ended - Duration: ${walkingDuration / 1000}s, Steps: $sessionStepCount, Valid: $isValidSession")

        if (isValidSession) {
            stepsTrackingManager.addWalkingSession(
                startTime = walkingStartTime,
                endTime = timestamp,
                stepCount = sessionStepCount,
                distance = calculateDistance(sessionStepCount)
            )
            Log.i(TAG, "✅ Walking session saved successfully")
        } else {
            Log.d(TAG, "❌ Walking session discarded (too short or too few steps)")
        }

        // Reset session state
        isCurrentlyWalking = false
        walkingStartTime = 0L
        sessionStepCount = 0
        lastActivityTime = 0L

        updateNotification()
    }

    private fun calculateDistance(steps: Int): Float {
        // Average step length is approximately 0.762 meters (2.5 feet)
        // This can be made configurable per user in the future
        val averageStepLength = 0.762f // meters
        return steps * averageStepLength
    }

    private fun stopSensorListening() {
        dataPointListener?.let { listener ->
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                Fitness.getSensorsClient(this, account)
                    .remove(listener)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Sensor listening stopped successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Failed to stop sensor listening: ${e.message}")
                    }
            }
            dataPointListener = null
        }
    }

    /**
     * Helper method to check if service is running properly
     */
    fun isServiceHealthy(): Boolean {
        return stepsTrackingManager.hasAllPermissions() &&
                dataPointListener != null &&
                GoogleSignIn.getLastSignedInAccount(this) != null
    }

    /**
     * Get current session information for debugging
     */
    fun getCurrentSessionInfo(): String {
        return "Walking: $isCurrentlyWalking, Steps: $sessionStepCount, " +
                "Duration: ${if (isCurrentlyWalking) (System.currentTimeMillis() - walkingStartTime) / 1000 else 0}s"
    }
}
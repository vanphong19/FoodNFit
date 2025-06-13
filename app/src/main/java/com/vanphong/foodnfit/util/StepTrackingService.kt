//package com.vanphong.foodnfit.util
//
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Build
//import android.os.IBinder
//import androidx.core.app.NotificationCompat
//import com.vanphong.foodnfit.repository.StepsTrackingRepository
//import kotlinx.coroutines.*
//
//class StepTrackingService : Service(), SensorEventListener {
//
//    private lateinit var sensorManager: SensorManager
//    private lateinit var sessionManager: StepSessionManager
//    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//
//    override fun onCreate() {
//        super.onCreate()
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
//        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
//
//        sessionManager = StepSessionManager(StepsTrackingRepository())
//
//        startForegroundService()
//
//        coroutineScope.launch {
//            while (true) {
//                sessionManager.checkAndEndSessionIfNeeded()
//                delay(5000)
//            }
//        }
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
//            val totalSteps = event.values[0].toInt()
//            sessionManager.onNewStep(totalSteps)
//        }
//    }
//
//    override fun onDestroy() {
//        sensorManager.unregisterListener(this)
//        coroutineScope.cancel()
//        super.onDestroy()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//
//    private fun startForegroundService() {
//        val channelId = "step_channel_id"
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Step Tracker",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Đang đếm bước chân")
//            .setContentText("Ứng dụng đang ghi nhận bước chân của bạn")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//
//        startForeground(1, notification)
//    }
//}

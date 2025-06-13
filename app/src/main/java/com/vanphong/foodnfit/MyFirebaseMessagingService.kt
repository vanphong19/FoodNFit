package com.vanphong.foodnfit

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vanphong.foodnfit.util.NotificationUtils


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService: FirebaseMessagingService(){

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            val title = message.data["title"] ?: "Reminder"
            val body = message.data["message"] ?: "You have a new reminder"

            // Kiểm tra quyền và hiển thị nếu đã cấp
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationUtils.showNotification(this, title, body)
            }
        }
    }
}
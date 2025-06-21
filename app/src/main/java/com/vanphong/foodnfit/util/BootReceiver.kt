package com.vanphong.foodnfit.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.vanphong.foodnfit.repository.StepsTrackingRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val stepsTrackingManager = StepsTrackingManager(context, StepsTrackingRepository())

            if (stepsTrackingManager.isTrackingEnabled() && stepsTrackingManager.hasAllPermissions()) {
                println("...Boot completed, restarting StepsTrackingService...")
                val serviceIntent = Intent(context, StepsTrackingService::class.java)
                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}
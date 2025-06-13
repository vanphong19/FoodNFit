package com.vanphong.foodnfit

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.databinding.ActivityMainBinding
//import com.vanphong.foodnfit.util.StepTrackingService

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val STEP_PERMISSION_REQUEST_CODE = 1001
    private val PREF_FIRST_TIME_PERMISSION = "first_time_step_permission"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController: NavController = findNavController(R.id.fragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        //checkStepPermissionOnFirstUse()
    }

//    private fun checkStepPermissionOnFirstUse() {
//        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
//        val firstTimeAsk = sharedPref.getBoolean(PREF_FIRST_TIME_PERMISSION, true)
//
//        val hasPermission = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACTIVITY_RECOGNITION
//        ) == PackageManager.PERMISSION_GRANTED
//
//        if (hasPermission) {
//            startStepService()
//        } else if (firstTimeAsk) {
//            // Lưu lại rằng đã hỏi rồi
//            sharedPref.edit().putBoolean(PREF_FIRST_TIME_PERMISSION, false).apply()
//            requestStepPermission()
//        }
//        // Nếu không phải lần đầu và không có quyền → không hỏi lại nữa
//    }
//
//    private fun requestStepPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
//                STEP_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    private fun startStepService() {
//        val intent = Intent(this, StepTrackingService::class.java)
//        ContextCompat.startForegroundService(this, intent)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == STEP_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("Permission", "ACTIVITY_RECOGNITION granted")
//                startStepService()
//                Toast.makeText(this, getString(R.string.permission_granted_msg), Toast.LENGTH_SHORT).show()
//            } else {
//                Log.d("Permission", "ACTIVITY_RECOGNITION denied")
//                Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_LONG).show()
//            }
//        }
//    }
}

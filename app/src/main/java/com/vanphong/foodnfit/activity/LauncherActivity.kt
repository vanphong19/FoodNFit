package com.vanphong.foodnfit.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsAnimation
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.Model.AuthResponse
import com.vanphong.foodnfit.Model.FcmTokenRequest
import com.vanphong.foodnfit.Model.RefreshTokenRequest
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.AdminActivity
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.network.service.FcmTokenService
import com.vanphong.foodnfit.repository.AuthRepository
import com.vanphong.foodnfit.repository.FcmTokenRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LauncherActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var progressDialog: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        authRepository = AuthRepository(RetrofitClient.authService)

        // Hiển thị ProgressBar ngay khi SplashScreen bắt đầu
        progressDialog = findViewById(R.id.progressBar)
        progressDialog.visibility = View.VISIBLE

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = prefs.getString("access_token", null)
        val refreshToken = prefs.getString("refresh_token", null)

        lifecycleScope.launch {
            if (!accessToken.isNullOrEmpty()) {
                // Kiểm tra token hợp lệ hoặc refresh token
                val authResponse = checkTokenValidOrRefresh(refreshToken)
                if (authResponse != null) {
                    val role = authResponse.role ?: "USER"
                    prefs.edit()
                        .putString("access_token", authResponse.accessToken)
                        .putString("refresh_token", authResponse.refreshToken)
                        .putString("role", role)
                        .apply()

                    // Điều hướng dựa trên role
                    val intent = if (role.equals("ADMIN", ignoreCase = true)) {
                        Intent(this@LauncherActivity, AdminActivity::class.java)
                    } else {
                        Intent(this@LauncherActivity, MainActivity::class.java)
                    }
                    startActivity(intent)
                } else {
                    // Token không hợp lệ → chuyển đến SignIn
                    startActivity(Intent(this@LauncherActivity, SignInActivity::class.java))
                }
            } else {
                // Chưa đăng nhập → chuyển đến SignIn
                startActivity(Intent(this@LauncherActivity, SignInActivity::class.java))
            }

            // Ẩn ProgressBar khi kết thúc và finish SplashActivity
            progressDialog.visibility = View.GONE
            finish()
        }
    }

    private suspend fun checkTokenValidOrRefresh(refreshToken: String?): AuthResponse? {
        if (refreshToken.isNullOrEmpty()) return null

        return try {
            val result = authRepository.refreshToken(RefreshTokenRequest(refreshToken))
            result.getOrNull()
        } catch (e: Exception) {
            null
        }
    }
}

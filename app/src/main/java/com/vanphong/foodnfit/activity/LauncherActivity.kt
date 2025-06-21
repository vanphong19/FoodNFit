package com.vanphong.foodnfit.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.model.AuthResponse
import com.vanphong.foodnfit.model.RefreshTokenRequest
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.admin.AdminActivity
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import com.vanphong.foodnfit.util.ColdStartHelper
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var progressDialog: ProgressBar
    private var statusText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        authRepository = AuthRepository(RetrofitClient.authService)

        progressDialog = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.tvStatus)
        progressDialog.visibility = View.VISIBLE

        ColdStartHelper.recordActivity(this)

        val appPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val onboardingCompleted = appPrefs.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }


        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val accessToken = prefs.getString("access_token", null)
        val refreshToken = prefs.getString("refresh_token", null)

        lifecycleScope.launch {
            if (!accessToken.isNullOrEmpty()) {
                // Kiểm tra cold start trước khi validate token
                if (ColdStartHelper.isPotentialColdStart(this@LauncherActivity)) {
                    showWarmUpProgress()

                    // ✅ FIX: Wrap callback trong coroutine
                    ColdStartHelper.warmUpIfNeeded(this@LauncherActivity) { success ->
                        lifecycleScope.launch {
                            if (success) {
                                checkTokenAndNavigate(refreshToken)
                            } else {
                                // Server không phản hồi, vẫn thử navigate
                                checkTokenAndNavigate(refreshToken)
                            }
                        }
                    }
                } else {
                    // Server sẵn sàng, kiểm tra token ngay
                    checkTokenAndNavigate(refreshToken)
                }
            } else {
                // Chưa đăng nhập → chuyển đến SignIn
                navigateToSignIn()
            }
        }
    }

    private fun showWarmUpProgress() {
        statusText?.visibility = View.VISIBLE

        ColdStartHelper.showWarmUpProgress(this) { message ->
            statusText?.text = message
        }
    }

    private suspend fun checkTokenAndNavigate(refreshToken: String?) {
        val authResponse = checkTokenValidOrRefresh(refreshToken)
        if (authResponse != null) {
            val role = authResponse.role ?: "USER"
            val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("access_token", authResponse.accessToken)
                .putString("refresh_token", authResponse.refreshToken)
                .putString("role", role)
                .apply()

            // Điều hướng dựa trên role
            val intent = if (role.equals("ADMIN", ignoreCase = true)) {
                Intent(this, AdminActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
        } else {
            // Token không hợp lệ → chuyển đến SignIn
            navigateToSignIn()
        }

        finishLauncher()
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finishLauncher()
    }

    private fun finishLauncher() {
        progressDialog.visibility = View.GONE
        statusText?.visibility = View.GONE
        finish()
    }

    private suspend fun checkTokenValidOrRefresh(refreshToken: String?): AuthResponse? {
        if (refreshToken.isNullOrEmpty()) return null

        return try {
            val result = authRepository.refreshToken(RefreshTokenRequest(refreshToken))
            result.getOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
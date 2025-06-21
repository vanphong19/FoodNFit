package com.vanphong.foodnfit.activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.MainActivity
import com.vanphong.foodnfit.model.FcmTokenRequest
import com.vanphong.foodnfit.admin.AdminActivity
import com.vanphong.foodnfit.viewModel.LoginViewModel
import com.vanphong.foodnfit.databinding.ActivitySignInBinding
import com.vanphong.foodnfit.repository.FcmTokenRepository
import kotlinx.coroutines.launch

class SignInActivity : BaseActivity() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var fcmTokenRepository: FcmTokenRepository  // Thay vì fcmTokenService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.loginViewModel = viewModel

        fcmTokenRepository = FcmTokenRepository()

        // Quan sát navigateMain để chuyển màn hình chính
        viewModel.navigateMain.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
                val hasShownToast = prefs.getBoolean("hasShownLoginSuccessToast", false)

                if (!hasShownToast) {
                    Toast.makeText(this, "Login thành công", Toast.LENGTH_SHORT).show()
                    prefs.edit().putBoolean("hasShownLoginSuccessToast", true).apply()
                }

                val role = prefs.getString("role", "USER") ?: "USER"

                val intent = when(role.uppercase()) {
                    "ADMIN" -> Intent(this, AdminActivity::class.java)
                    else -> Intent(this, MainActivity::class.java)
                }
                setUpNotification()
                startActivity(intent)
                finish()
                viewModel.onNavigationComplete()
            }
        }

        // Quan sát navigateSignUp để chuyển sang đăng ký
        viewModel.navigateSignUp.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                viewModel.onNavigateSignUpComplete()
            }
        }

        binding.forgotPass.setOnClickListener {
            viewModel.onClickForgotPassword()
        }

        viewModel.navigateForgotPassword.observe(this) { go ->
            if (go) {
                startActivity(Intent(this, ForgetPasswordActivity::class.java))
                viewModel.onNavigateForgotPasswordComplete()
            }
        }

        // Quan sát lỗi để hiện Toast
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpNotification() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(Constants.MessageNotificationKeys.TAG, "Lỗi lấy token", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            sendFcmTokenToBackend(token)
        })
    }

    private fun sendFcmTokenToBackend(token: String) {
        val request = FcmTokenRequest(token)

        // Gọi API để gửi token đến backend từ FcmTokenRepository
        lifecycleScope.launch {
            val result = fcmTokenRepository.saveToken(request)
            result.onSuccess {
                Log.d("FCM", "Token đã được lưu thành công")
            }.onFailure { exception ->
                Log.e("FCM", "Lưu token thất bại: ${exception.message}")
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}

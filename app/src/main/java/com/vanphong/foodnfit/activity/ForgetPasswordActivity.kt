package com.vanphong.foodnfit.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.databinding.ActivityForgetPasswordBinding
import com.vanphong.foodnfit.viewModel.ForgetPasswordViewModel

class ForgetPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val viewModel: ForgetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.btnSendOtp.setOnClickListener {
            viewModel.onClickSendOtp()
        }

        binding.btnResetPassword.setOnClickListener {
            viewModel.onClickResetPassword()
        }

        viewModel.status.observe(this) { message ->
            binding.textStatus.text = message
            binding.textStatus.visibility = if (message.isNullOrBlank()) View.GONE else View.VISIBLE
        }

        viewModel.isOtpSent.observe(this) { sent ->
            if (sent) {
                binding.btnSendOtp.visibility = View.GONE
                binding.layoutOtp.visibility = View.VISIBLE
                binding.layoutNewPassword.visibility = View.VISIBLE
                binding.btnResetPassword.visibility = View.VISIBLE
            }
        }

        viewModel.navigateToLogin.observe(this) { go ->
            if (go) {
                Toast.makeText(this, "Mật khẩu đã được cập nhật", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                viewModel.onNavigatedToLogin()
                finish()
            }
        }
    }
}

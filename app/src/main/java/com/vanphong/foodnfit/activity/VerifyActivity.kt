package com.vanphong.foodnfit.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vanphong.foodnfit.BaseActivity
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.component.OtpEditText
import com.vanphong.foodnfit.databinding.ActivityVerifyBinding
import com.vanphong.foodnfit.viewModel.OtpViewModel
class VerifyActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyBinding
    private val viewModel: OtpViewModel by viewModels()
    private lateinit var otpFields: List<OtpEditText>
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify)
        email = intent.getStringExtra("email") ?: ""

        otpFields = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4, binding.otp5, binding.otp6)

        setupOtpInputs()

        viewModel.verificationResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.otp_success), Toast.LENGTH_SHORT).show()
                // Chuyển sang màn hình tiếp theo (ví dụ LoginActivity)
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.resend.setOnClickListener {
            viewModel.resendOtp(email)
        }

        binding.btnVerify.setOnClickListener {
            val otp = getOtp()
            if (otp.length == 6) {
                viewModel.verifyOtp(email, otp)
            } else {
                Toast.makeText(this, getString(R.string.otp_incomplete), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupOtpInputs() {
        for (i in otpFields.indices) {
            otpFields[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < otpFields.lastIndex) {
                        otpFields[i + 1].requestFocus()
                    } else if (s?.isEmpty() == true && i > 0) {
                        otpFields[i - 1].requestFocus()
                    }
                    viewModel.updateOtp(getOtp())
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        otpFields[0].setOnPasteListener { pastedText ->
            if (pastedText.length == 6 && pastedText.all { it.isDigit() }) {
                for (i in otpFields.indices) {
                    otpFields[i].setText(pastedText[i].toString())
                }
                otpFields.last().requestFocus()
            } else {
                Toast.makeText(this, getString(R.string.otp_invalid), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getOtp(): String {
        return otpFields.joinToString("") { it.text.toString().trim() }
    }
}

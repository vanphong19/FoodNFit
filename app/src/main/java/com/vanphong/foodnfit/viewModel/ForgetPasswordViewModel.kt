package com.vanphong.foodnfit.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.ResendOtpRequest
import com.vanphong.foodnfit.model.ResetPasswordRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import com.vanphong.foodnfit.util.isEmailValid
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(application: Application): AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitClient.authService)

    val email = MutableLiveData("")
    val otp = MutableLiveData("")
    val newPassword = MutableLiveData("")

    private val _status = MutableLiveData<String?>()
    val status: LiveData<String?> = _status

    private val _isOtpSent = MutableLiveData(false)
    val isOtpSent: LiveData<Boolean> = _isOtpSent

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun onClickSendOtp() {
        val emailValue = email.value ?: ""
        if (!isEmailValid(emailValue)) {
            _status.postValue("Email không hợp lệ")
            return
        }

        viewModelScope.launch {
            val result = repository.forgotPassword(ResendOtpRequest(emailValue))
            result.onSuccess {
                _status.postValue("Đã gửi OTP đến email")
                _isOtpSent.postValue(true)
            }.onFailure {
                _status.postValue(it.message ?: "Gửi OTP thất bại")
            }
        }
    }

    fun onClickResetPassword() {
        val emailValue = email.value ?: ""
        val otpValue = otp.value ?: ""
        val newPass = newPassword.value ?: ""

        if (otpValue.length != 6 || newPass.length < 6) {
            _status.postValue("OTP hoặc mật khẩu không hợp lệ")
            return
        }

        viewModelScope.launch {
            val req = ResetPasswordRequest(emailValue, otpValue, newPass)
            val result = repository.resetPassword(req)
            result.onSuccess {
                _status.postValue("Đặt lại mật khẩu thành công!")
                _navigateToLogin.postValue(true) // 👈 Trigger chuyển màn
            }.onFailure {
                _status.postValue(it.message ?: "Đặt lại mật khẩu thất bại")
            }
        }
    }

    fun onNavigatedToLogin() {
        _navigateToLogin.value = false
    }
}

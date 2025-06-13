package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.Model.OtpVerificationRequest
import com.vanphong.foodnfit.Model.ResendOtpRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {
    private val repository = AuthRepository(RetrofitClient.authService)

    private val _otpCode = MutableLiveData<String>()
    val otpCode: LiveData<String> get() = _otpCode

    private val _verificationResult = MutableLiveData<Boolean>()
    val verificationResult: LiveData<Boolean> get() = _verificationResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun updateOtp(code: String) {
        _otpCode.value = code
    }

    fun verifyOtp(email: String, code: String) {
        viewModelScope.launch {
            val result = repository.verifyOtp(OtpVerificationRequest(email, code))
            if (result.isSuccess) {
                _verificationResult.postValue(true)
            } else {
                _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Xác minh OTP thất bại")
            }
        }
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            val result = repository.resendOtp(ResendOtpRequest(email))
            if (result.isSuccess) {
                _errorMessage.postValue("Mã OTP mới đã được gửi lại")
            } else {
                _errorMessage.postValue(result.exceptionOrNull()?.message ?: "Không thể gửi lại mã OTP")
            }
        }
    }
}

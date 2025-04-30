package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtpViewModel: ViewModel() {
    private val _otpCode = MutableLiveData<String>()
    val otpCode:LiveData<String> get() = _otpCode

    fun updateOtp(code: String){
        _otpCode.value = code
    }

    fun verifyOtp(email: String, code: String) {
        Log.d("OTP", "Verify OTP: $code cho email $email")
    }
}
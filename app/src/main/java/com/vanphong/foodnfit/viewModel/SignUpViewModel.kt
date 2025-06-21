package com.vanphong.foodnfit.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.RegisterRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import com.vanphong.foodnfit.util.isEmailValid
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitClient.authService)

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> get() = _confirmPassword

    private val _navigateToOtp = MutableLiveData<Boolean>()
    val navigateToOtp: LiveData<Boolean> = _navigateToOtp

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun onEmailChanged(newEmail: CharSequence) {
        _email.value = newEmail.toString()
    }

    fun onPasswordChanged(newPassword: CharSequence) {
        _password.value = newPassword.toString()
    }

    fun onConfirmPasswordChanged(newConfirmPassword: CharSequence) {
        _confirmPassword.value = newConfirmPassword.toString()
    }

    fun registerUser() {
        val emailValue = _email.value ?: ""
        val passwordValue = _password.value ?: ""
        val confirmPasswordValue = _confirmPassword.value ?: ""

        if (!isEmailValid(emailValue)) {
            _error.value = "Email không đúng định dạng"
            return
        }

        if (passwordValue != confirmPasswordValue) {
            _error.value = "Mật khẩu và xác nhận mật khẩu không khớp"
            return
        }

        viewModelScope.launch {
            val result = repository.register(RegisterRequest(emailValue, passwordValue, confirmPasswordValue))
            if (result.isSuccess) {
                Log.d("REGISTER", "Register thành công")
                _navigateToOtp.postValue(true)
            } else {
                Log.e("REGISTER", "Lỗi: ${result.exceptionOrNull()?.message}")
                _error.postValue(result.exceptionOrNull()?.message ?: "Đăng ký thất bại")
            }
        }
    }
}

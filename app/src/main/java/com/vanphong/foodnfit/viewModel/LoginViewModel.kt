package com.vanphong.foodnfit.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.InverseMethod
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.Model.LoginRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import com.vanphong.foodnfit.util.isEmailValid
import kotlinx.coroutines.launch

class LoginViewModel(application: Application): AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitClient.authService)

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _navigateMain = MutableLiveData<Boolean>()
    val navigateMain: LiveData<Boolean> get() = _navigateMain

    private val _navigateSignUp = MutableLiveData<Boolean>()
    val navigateSignUp:LiveData<Boolean> get() = _navigateSignUp

    private val _loginError = MutableLiveData<String?>()
    val error: LiveData<String?> = _loginError

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess
    private val _navigateForgotPassword = MutableLiveData<Boolean>()
    val navigateForgotPassword: LiveData<Boolean> = _navigateForgotPassword

    fun onClickForgotPassword() {
        _navigateForgotPassword.value = true
    }

    fun onNavigateForgotPasswordComplete() {
        _navigateForgotPassword.value = false
    }

    fun onEmailChanged(newEmail: CharSequence) {
        // Optional: Add validation or logic before updating
        if (_email.value != newEmail.toString()) {
            _email.value = newEmail.toString()
            Log.d("LoginViewModel", "Email updated to: ${_email.value}")
        }
    }

    fun onPasswordChanged(newPassword: CharSequence) {
        if (_password.value != newPassword.toString()) {
            _password.value = newPassword.toString()
            Log.d("LoginViewModel", "Password updated") // Avoid logging password itself
        }
    }
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        checkAlreadyLoggedIn()
    }

    private fun checkAlreadyLoggedIn() {
        val prefs = getApplication<Application>().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)
        if (!token.isNullOrEmpty()) {
            _navigateMain.postValue(true)
        }
    }

    fun onClickLogin() {
        viewModelScope.launch {
            val emailValue = _email.value ?: ""
            val passwordValue = _password.value ?: ""

            if (emailValue.isBlank() || passwordValue.isBlank()) {
                _loginError.postValue("Email và mật khẩu không được để trống")
                return@launch
            }
            if (!isEmailValid(emailValue)) {
                _loginError.postValue("Email không đúng định dạng")
                return@launch
            }

            _isLoading.postValue(true)
            val request = LoginRequest(emailValue, passwordValue)
            val result = repository.login(request)
            result.onSuccess { authResponse ->
                saveTokens(authResponse.accessToken, authResponse.refreshToken, authResponse.role)
                _loginSuccess.postValue(true)
                _loginError.postValue(null)
                _navigateMain.postValue(true)
            }.onFailure { throwable ->
                _loginError.postValue(throwable.message ?: "Đăng nhập thất bại")
            }
            _isLoading.postValue(false)
        }
    }


    private fun saveTokens(accessToken: String, refreshToken: String, role: String) {
        val prefs = getApplication<Application>().getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .putString("role", role)
            .apply()
    }
    fun onNavigationComplete() {
        _navigateMain.value = false
    }
    fun onClickSignUp(){
        _navigateSignUp.value = true
    }
    fun onNavigateSignUpComplete(){
        _navigateSignUp.value = false
    }
}
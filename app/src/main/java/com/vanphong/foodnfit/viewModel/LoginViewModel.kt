package com.vanphong.foodnfit.viewModel

import android.util.Log
import android.widget.Toast
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _navigateMain = MutableLiveData<Boolean>()
    val navigateMain: LiveData<Boolean> get() = _navigateMain

    private val _navigateSignUp = MutableLiveData<Boolean>()
    val navigateSignUp:LiveData<Boolean> get() = _navigateSignUp

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
    fun onClickLogin(){
        if(_email.value == "abc@gmail.com" && _password.value == "123456"){
            _navigateMain.value = true
            Log.d("loi", "success")
        }
        else {
            Log.d("loi", "lor")
        }
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
package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.vanphong.foodnfit.model.ChangePasswordRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val authRepository = AuthRepository(RetrofitClient.authService)

    private val _current = MutableLiveData<String>()
    val current: LiveData<String> get() = _current

    private val _new = MutableLiveData<String>()
    val new: LiveData<String> get() = _new

    private val _confirm = MutableLiveData<String>()
    val confirm: LiveData<String> get() = _confirm

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun setCurrentPassword(password: String) {
        _current.value = password
    }

    fun setNewPassword(password: String) {
        _new.value = password
    }

    fun setConfirmPassword(password: String) {
        _confirm.value = password
    }

    fun changePassword(token: String) {
        val oldPassword = _current.value ?: ""
        val newPassword = _new.value ?: ""
        val confirmPassword = _confirm.value ?: ""

        if (newPassword != confirmPassword) {
            _message.value = "Mật khẩu mới và xác nhận không khớp"
            return
        }

        val request = ChangePasswordRequest(
            oldPassword = oldPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmPassword
        )

        Log.d("ChangePasswordRequest", Gson().toJson(request))

        viewModelScope.launch {
            try {
                val result = authRepository.changePassword(token, request)
                result.onSuccess {
                    _message.postValue("Đổi mật khẩu thành công")
                }.onFailure {
                    Log.e("ChangePassword", "Lỗi khi đổi mật khẩu", it)
                    _message.postValue(it.message ?: "Lỗi không xác định")
                }
            } catch (e: Exception) {
                Log.e("ChangePassword", "Lỗi không xác định: ${e.message}")
                _message.postValue("Lỗi không xác định")
            }
        }
    }
}

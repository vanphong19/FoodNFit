package com.vanphong.foodnfit.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonViewModel : ViewModel() {

    private val _navigateSetting = MutableLiveData<Boolean>()
    val navigateSetting: LiveData<Boolean> get() = _navigateSetting

    private val _navigateProfile = MutableLiveData<Boolean>()
    val navigateProfile: LiveData<Boolean> get() = _navigateProfile

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    fun onClickNavigateSetting() {
        _navigateSetting.value = true
    }

    fun completeNavigateSetting() {
        _navigateSetting.value = false
    }

    fun onCLickNavigateProfile() {
        _navigateProfile.value = true
    }

    fun completeNavigateProfile() {
        _navigateProfile.value = false
    }

    // Hàm gọi khi người dùng bấm logout
    fun onClickLogout(context: Context) {
        // Xóa token trong SharedPreferences
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().clear().putBoolean("hasShownLoginSuccessToast", false).apply()

        // Cập nhật trạng thái logout thành công
        _logoutSuccess.value = true
    }

    fun completeLogout() {
        _logoutSuccess.value = false
    }
}

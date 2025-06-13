package com.vanphong.foodnfit.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.util.LogoutEventBus
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> = _logout

    init {
        // Lắng nghe sự kiện logout toàn cục
        viewModelScope.launch {
            LogoutEventBus.logoutFlow.collect {
                // Xóa token, data auth khi logout
                val prefs = getApplication<Application>().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
                prefs.edit().clear().apply()

                // Cập nhật trạng thái logout
                _logout.postValue(true)
            }
        }
    }

    // Các hàm xử lý auth khác nếu cần (login, refresh token, check trạng thái...)
}
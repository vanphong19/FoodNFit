package com.vanphong.foodnfit.admin.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.HistoryResponse
import com.vanphong.foodnfit.repository.UserRepository
import kotlinx.coroutines.launch

class AccountInfoViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val _avatar = MutableLiveData<String?>()
    val avatar: LiveData<String?> get() = _avatar

    private val _fullname = MutableLiveData<String?>()
    val fullname: LiveData<String?> get() = _fullname

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> get() = _email

    private val _gender = MutableLiveData<Boolean?>()
    val gender: LiveData<Boolean?> get() = _gender

    private val _birthday = MutableLiveData<String?>()
    val birthday: LiveData<String?> get() = _birthday

    private val _blocked = MutableLiveData<Boolean>()
    val blocked: LiveData<Boolean> = _blocked

    private val _accountHistory = MutableLiveData<List<HistoryResponse>?>()
    val accountHistory: LiveData<List<HistoryResponse>?> get() = _accountHistory

    // Repository giả định
    private val userRepository = UserRepository()

    // Hàm load dữ liệu theo ID
    fun getAccountById(accountId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.getUserById(accountId)

                result.onSuccess { account ->
                    _avatar.value = account.avatarUrl
                    _fullname.value = account.fullname
                    _email.value = account.email
                    _gender.value = account.gender
                    _birthday.value = account.birthday
                    _accountHistory.value = account.history
                    _blocked.value = account.blocked
                }

                result.onFailure { e ->
                    Log.e("AccountInfoViewModel", "Lỗi khi load account: ${e.message}")
                }
            }
            finally {
                _isLoading.value = false
            }
        }
        Log.d("history", accountHistory.toString())
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _lockAccountResult = MutableLiveData<Result<Boolean>>()
    val lockAccountResult: LiveData<Result<Boolean>> get() = _lockAccountResult

    private val _notify = MutableLiveData<String?>()
    val notify: LiveData<String?> get() = _notify

    fun lockAccount(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.lockUser(userId)  // Result<UserResponse>
                val booleanResult: Result<Boolean> = result.fold(
                    onSuccess = {
                        Result.success(true)   // gói thành Result<Boolean>
                    },
                    onFailure = { throwable ->
                        Log.e("LockUser", "Error locking user", throwable)
                        Result.failure(throwable) // gói lỗi thành Result
                    }
                )
                _lockAccountResult.value = booleanResult
            } catch (e: Exception) {
                Log.e("LockUser", "Unexpected error", e)
                _lockAccountResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun remove(userId: String){
        viewModelScope.launch {
            val result = userRepository.deleteUser(userId)
            result.onSuccess {
                _notify.postValue(context.getString(R.string.remove_account_success))
            }
            .onFailure {
                _notify.postValue(null)
            }
        }
    }
}

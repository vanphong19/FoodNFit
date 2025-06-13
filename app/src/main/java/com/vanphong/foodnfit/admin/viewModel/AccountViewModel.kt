package com.vanphong.foodnfit.admin.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.Model.User
import com.vanphong.foodnfit.Model.UserResponse
import com.vanphong.foodnfit.repository.UserRepository
import kotlinx.coroutines.launch

class AccountViewModel:ViewModel() {
    private val userRepository = UserRepository()
    private val _accountList = MutableLiveData<List<UserResponse>>()
    val accountList: LiveData<List<UserResponse>> get() = _accountList
    private val _navigateCreate = MutableLiveData<Boolean>()
    val navigateCreate: LiveData<Boolean> get() = _navigateCreate

    private val _search = MutableLiveData<String?>()
    val search: LiveData<String?> get() = _search
    private val _gender = MutableLiveData<Boolean?>()
    val gender: LiveData<Boolean?> get() = _gender

    private val _blocked = MutableLiveData<Boolean?>()
    val blocked: LiveData<Boolean?> get() = _blocked

     var currentPage = 0
     var isLoading = false
     var isLastPage = false
     val pageSize = 10
    fun setGender(gender: Boolean?)
    {
        _gender.value = gender
    }
    fun setBlocked(blocked: Boolean?){
        _blocked.postValue(blocked)
    }
    fun setSearch(keyword: CharSequence?) {
        _search.value = keyword?.toString()?.trim()
        refreshList()
    }
    fun loadNextPage() {
        if (isLoading || isLastPage) return

        isLoading = true

        viewModelScope.launch {
            val result = userRepository.getAllUsers(
                search = _search.value ?: "",
                gender = _gender.value,
                block = _blocked.value,
                page = currentPage,
                size = pageSize
            )

            result.onSuccess { page ->
                val newList = (_accountList.value ?: emptyList()) + page.content
                _accountList.value = newList

                Log.d("data", newList.toString())


                isLastPage = page.content.size < pageSize
                currentPage++
            }
            result.onFailure { throwable ->
                Log.e("loadNextPage", "Lấy dữ liệu thất bại", throwable)
            }

            isLoading = false
        }
    }

    fun refreshList() {
        currentPage = 0
        isLastPage = false
        _accountList.value = emptyList()
        loadNextPage()
    }

    fun navigateCreate(){
        _navigateCreate.value = true
    }
    fun completeCreate(){
        _navigateCreate.value = false
    }
}
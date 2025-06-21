package com.vanphong.foodnfit.admin.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.UserRequest
import com.vanphong.foodnfit.model.UserUpdateRequest
import com.vanphong.foodnfit.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

class AddEditAccountViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _fullname = MutableLiveData<String?>()
    val fullname: LiveData<String?> get() = _fullname

    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> get() = _email

    private val _gender = MutableLiveData<Boolean?>()
    val gender: LiveData<Boolean?> get() = _gender

    private val _birthday = MutableLiveData<String?>()
    val birthday: LiveData<String?> get() = _birthday

    private val _avatar = MutableLiveData<String?>()
    val avatar: LiveData<String?> get() = _avatar

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _password = MutableLiveData<String?>()
    val password: LiveData<String?> get() = _password

    private val _saveResult = MutableLiveData<Result<Boolean>>()
    val saveResult: LiveData<Result<Boolean>> get() = _saveResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>get() = _error

    // Setters for form data
    fun setFullname(fullname: String) {
        _fullname.value = fullname
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setGender(gender: Boolean?) {
        _gender.value = gender
    }

    fun setBirthday(birthday: String) {
        _birthday.value = birthday
    }

    fun setAvatar(avatar: String) {
        _avatar.value = avatar
    }

    fun loadUserData(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.getUserById(userId)
                result.onSuccess { user ->
                    _fullname.value = user.fullname
                    _email.value = user.email
                    _gender.value = user.gender
                    _birthday.value = user.birthday
                    _avatar.value = user.avatarUrl
                }
                result.onFailure { error ->
                    Log.e("AddEditAccountViewModel", "Error loading user data: ${error.message}")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadImage(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = userRepository.uploadImageFromFile(file)
                if (response.isSuccessful) {
                    val url = response.body()?.url
                    if (url != null) {
                        _avatar.postValue(url)  // Lưu URL ảnh public
                        _error.postValue(null)
                    } else {
                        _error.postValue("Upload ảnh thất bại: URL rỗng")
                    }
                } else {
                    _error.postValue("Upload ảnh thất bại: code ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Upload ảnh thất bại: ${e.message}")
            }
        }
    }

    fun createUser(
    ) {
        val request = setRequest() ?: return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.createUser(request)

                result.onSuccess {
                    _saveResult.value = Result.success(true)
                    Log.d("AddEditAccountViewModel", "User created successfully")
                }

                result.onFailure { error ->
                    _saveResult.value = Result.failure(error)
                    Log.e("AddEditAccountViewModel", "Error creating user: ${error.message}")
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
                Log.e("AddEditAccountViewModel", "Unexpected error creating user", e)
            } finally {
                _isLoading.value = false
            }
        }

        Log.d("CreateUserRequest", "Request = ${request}")
    }

    fun updateUser(userId: String) {
        val request = setUpdateRequest() ?: return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(userId, request)

                result.onSuccess {
                    _saveResult.value = Result.success(true)
                    Log.d("AddEditAccountViewModel", "User updated successfully")
                }

                result.onFailure { error ->
                    _saveResult.value = Result.failure(error)
                    Log.e("AddEditAccountViewModel", "Error updating user: ${error.message}")
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
                Log.e("AddEditAccountViewModel", "Unexpected error updating user", e)
            } finally {
                _isLoading.value = false
            }
        }
        Log.d("CreateUserRequest", "Request = $request")

    }
    private fun setUpdateRequest(): UserUpdateRequest? {
        val fullname = _fullname.value
        val gender = _gender.value
        val birthdayStr = _birthday.value
        val avatar = _avatar.value

        // Kiểm tra rỗng
        if (fullname.isNullOrBlank() || birthdayStr.isNullOrBlank() || avatar.isNullOrBlank() || gender == null) {
            return null
        }

        try {
            LocalDate.parse(birthdayStr)  // để validate thôi
        } catch (e: DateTimeParseException) {
            _error.value = "Ngày sinh không đúng định dạng yyyy-MM-dd"
            return null
        }

        return UserUpdateRequest(
            fullname = fullname,
            gender = gender,
            birthday = birthdayStr,
            avatarUrl = avatar
        )
    }


    private fun setRequest(): UserRequest? {
        val fullname = _fullname.value
        val email = _email.value
        val gender = _gender.value
        val birthdayStr = _birthday.value
        val avatar = _avatar.value
        val password = _password.value

        // Kiểm tra rỗng
        if (fullname.isNullOrBlank() || email.isNullOrBlank() || birthdayStr.isNullOrBlank() || avatar.isNullOrBlank() || gender == null) {
            return null
        }

        try {
            LocalDate.parse(birthdayStr)  // để validate thôi
        } catch (e: DateTimeParseException) {
            _error.value = "Ngày sinh không đúng định dạng yyyy-MM-dd"
            return null
        }

        return UserRequest(
            fullname = fullname,
            email = email,
            gender = gender,
            birthday = birthdayStr,
            password = password!!,
            avatarUrl = avatar
        )
    }

}
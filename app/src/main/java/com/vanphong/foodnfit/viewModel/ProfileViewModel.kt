package com.vanphong.foodnfit.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.UserProfileRequest
import com.vanphong.foodnfit.model.UserUpdateRequest
import com.vanphong.foodnfit.repository.UserProfileRepository
import com.vanphong.foodnfit.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val profileRepository = UserProfileRepository()
    private val userRepository = UserRepository()
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val _avtUrl = MutableLiveData<String?>()
    val avtUrl: LiveData<String?> = _avtUrl
    private val _name = MutableLiveData<String?>()
    val name: LiveData<String?> = _name
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email
    private val _mealGoal = MutableLiveData<String>()
    val mealGoal: LiveData<String> get() = _mealGoal
    private val _exerciseGoal = MutableLiveData<String>()
    val exerciseGoal: LiveData<String> get() = _exerciseGoal
    private val _age = MutableLiveData<String>()
    val age: LiveData<String> get() = _age
    private val _birthday = MutableLiveData<String>()
    private val _tdee = MutableLiveData<Float>()
    val tdee: LiveData<Float> get() = _tdee
    val birthday: LiveData<String> get() = _birthday
    private val _height = MutableLiveData<String>()
    val height: LiveData<String> get() = _height
    private val _weight = MutableLiveData<String>()
    val weight: LiveData<String> get() = _weight
    private val _gender = MutableLiveData<Boolean>()
    val gender: LiveData<Boolean> get() = _gender

    private val _loading = MutableLiveData<Boolean>()
    val loading:LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun setAvtUrl(value: String?) {
        _avtUrl.value = value
    }

    fun setName(value: String?) {
        _name.value = value
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setMealGoal(value: String) {
        _mealGoal.value = value
    }

    fun setExerciseGoal(value: String) {
        _exerciseGoal.value = value
    }

    fun setAge(value: String) {
        _age.value = value
    }

    fun setBirthday(value: String) {
        _birthday.value = value
    }

    fun setHeight(value: String) {
        _height.value = value
    }

    fun setWeight(value: String) {
        _weight.value = value
    }

    fun setGender(value: Boolean) {
        _gender.value = value
    }

    fun loadInfo() {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = profileRepository.getLatest()
                result.onSuccess { profile ->
                    withContext(Dispatchers.Main) {
                        _avtUrl.value = profile.avtUrl
                        _name.value = profile.fullname
                        _email.value = profile.email
                        _gender.value = profile.gender
                        _mealGoal.value = profile.mealGoal
                        _exerciseGoal.value = profile.exerciseGoal
                        _age.value = calculateAge(profile.birthday).toString()
                        _birthday.value = profile.birthday
                        _height.value = profile.height.toString()
                        _weight.value = profile.weight.toString()
                        _tdee.value = profile.tdee
                    }
                }.onFailure { e ->
                    withContext(Dispatchers.Main) {
                        _error.value = e.message
                        Log.e("profileViewModel", "Lỗi: ${e.message}")
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _loading.value = false
                }
            }
        }
    }

    fun updateGoal(mealGoal: String, exerciseGoal: String) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Tạm thời dùng height, weight, tdee mặc định (nếu chưa có)
                val heightValue = _height.value?.toFloatOrNull() ?: 0f
                val weightValue = _weight.value?.toFloatOrNull() ?: 0f
                val tdeeValue = _tdee.value?: 0f // hoặc tính tdee nếu bạn đã có công thức

                val request = UserProfileRequest(
                    height = heightValue,
                    weight = weightValue,
                    tdee = tdeeValue,
                    mealGoal = mealGoal,
                    exerciseGoal = exerciseGoal
                )

                val result = profileRepository.create(request)

                withContext(Dispatchers.Main) {
                    result.onSuccess {
                        // Cập nhật lại LiveData nếu cần
                        _mealGoal.value = mealGoal
                        _exerciseGoal.value = exerciseGoal
                    }.onFailure {
                        _error.value = it.message
                        Log.e("ProfileViewModel", "Update goal failed: ${it.message}")
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _loading.value = false
                }
            }
        }
    }

    fun updateInfo(
        fullname: String,
        gender: Boolean,
        birthday: String,
        height: String,
        weight: String
    ) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val heightValue = height.toFloatOrNull() ?: 0f
                val weightValue = weight.toFloatOrNull() ?: 0f
                val tdeeValue = calculateTdee(heightValue, weightValue) // hoặc gán mặc định nếu chưa có công thức
                val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val userId = auth.getString("userId", null)?.takeIf { it.isNotBlank() } ?: "0"
                // 1. Gửi UserUpdateRequest
                val userUpdateRequest = UserUpdateRequest(
                    fullname = fullname,
                    gender = gender,
                    birthday = birthday,
                    avatarUrl = _avtUrl.value
                )
                val userUpdateResponse = userRepository.updateUser(userId, userUpdateRequest)
                Log.d("updateresponse", userUpdateResponse.toString())

                // 2. Gửi UserProfileRequest
                val profileRequest = UserProfileRequest(
                    height = heightValue,
                    weight = weightValue,
                    tdee = tdeeValue,
                    mealGoal = _mealGoal.value ?: "",
                    exerciseGoal = _exerciseGoal.value ?: ""
                )
                val profileResult = profileRepository.create(profileRequest)

                withContext(Dispatchers.Main) {
                    if (userUpdateResponse.isSuccess && profileResult.isSuccess) {
                        // Cập nhật lại LiveData nếu cần
                        _name.value = fullname
                        _gender.value = gender
                        _birthday.value = birthday
                        _height.value = height
                        _weight.value = weight
                    } else {
                        _error.value = "Update failed"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = e.message
                    Log.e("ProfileViewModel", "UpdateInfo error: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _loading.value = false
                }
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
                        _avtUrl.postValue(url)  // Lưu URL ảnh public
                        _error.postValue("Successfully")
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

    private fun calculateTdee(height: Float, weight: Float): Float {
        val birthdayValue = _birthday.value
        val genderValue = _gender.value

        if (birthdayValue == null || genderValue == null) {
            return 0f // Không đủ dữ liệu
        }

        val age = calculateAge(birthdayValue)

        // BMR: Basal Metabolic Rate
        val bmr = if (!genderValue) {
            // true: Nam
            10 * weight + 6.25f * height - 5 * age + 5
        } else {
            // false: Nữ
            10 * weight + 6.25f * height - 5 * age - 161
        }

        val activityFactor = 1.375f // tạm giả định vận động nhẹ

        return bmr * activityFactor
    }


    private fun calculateAge(birthday: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birthday, formatter)
        val today = LocalDate.now()
        return Period.between(birthDate, today).years
    }
}
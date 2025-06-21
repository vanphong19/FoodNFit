package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.FoodLogResponse
import com.vanphong.foodnfit.model.UserGoalResponse
import com.vanphong.foodnfit.repository.FoodLogRepository
import com.vanphong.foodnfit.repository.UserGoalRepository // Giả sử bạn có repository này
import kotlinx.coroutines.launch

class FoodLogViewModel: ViewModel() {
    private val foodLogRepository = FoodLogRepository()
    private val userGoalRepository = UserGoalRepository() // Cần repo này để lấy mục tiêu

    // Dữ liệu chi tiết của bữa ăn
    private val _mealInfo = MutableLiveData<FoodLogResponse>()
    val mealInfo: LiveData<FoodLogResponse> = _mealInfo

    // Dữ liệu mục tiêu của người dùng
    private val _userGoal = MutableLiveData<UserGoalResponse>()
    val userGoal: LiveData<UserGoalResponse> = _userGoal

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadMealInformation(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Gọi cả 2 API
            val mealResult = foodLogRepository.getFoodLogById(id)
            val goalResult = userGoalRepository.getLatest()

            mealResult.onSuccess { response ->
                _mealInfo.value = response
            }.onFailure { exception ->
                _error.value = "Failed to load meal details: ${exception.message}"
            }

            goalResult.onSuccess { response ->
                _userGoal.value = response
            }.onFailure { exception ->
                _error.value = "Failed to load user goals: ${exception.message}"
            }

            _isLoading.value = false
        }
    }
}
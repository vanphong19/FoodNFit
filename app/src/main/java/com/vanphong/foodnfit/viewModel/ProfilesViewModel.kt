package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.MonthlyProfileResponse
import com.vanphong.foodnfit.model.WeightHistoryData
import com.vanphong.foodnfit.repository.UserProfileRepository
import kotlinx.coroutines.launch

class ProfilesViewModel : ViewModel() {
    private val profileRepository = UserProfileRepository()

    private val _monthlyStatistics = MutableLiveData<MonthlyProfileResponse?>()
    val monthlyStatistics: LiveData<MonthlyProfileResponse?> get() = _monthlyStatistics

    private val _weightHistories = MutableLiveData<List<WeightHistoryData>>()
    val weightHistories: LiveData<List<WeightHistoryData>> get() = _weightHistories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Hàm này được gọi từ Fragment sau khi có dữ liệu mới để cập nhật cho RecyclerView
    fun setWeightHistories(data: List<WeightHistoryData>) {
        _weightHistories.value = data
    }

    fun getMonthlyStatistics(year: Int, month: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = profileRepository.getMonthlyStatistics(year, month)
                result.fold(
                    onSuccess = { response ->
                        _monthlyStatistics.value = response
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Có lỗi xảy ra khi tải dữ liệu"
                        _monthlyStatistics.value = null // Xóa dữ liệu cũ nếu có lỗi
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Có lỗi xảy ra"
                _monthlyStatistics.value = null // Xóa dữ liệu cũ nếu có lỗi
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
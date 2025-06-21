package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.vanphong.foodnfit.model.WeeklyNutritionResponse
import com.vanphong.foodnfit.repository.FoodLogRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CaloriesStatisticViewModel : ViewModel() {
    private val TAG = "CaloriesVM"
    private val foodLogRepository = FoodLogRepository()

    // --- State LiveData ---
    private val _weeklyNutritionData = MutableLiveData<WeeklyNutritionResponse?>()
    val weeklyNutritionData: LiveData<WeeklyNutritionResponse?> get() = _weeklyNutritionData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Thêm lại LiveData cho trạng thái rỗng
    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    // --- Input LiveData ---
    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val dateObserver = Observer<LocalDate> { date ->
        fetchWeeklySummary(date)
    }

    init {
        _selectedDate.observeForever(dateObserver)
        _selectedDate.value = LocalDate.now()
    }

    private fun fetchWeeklySummary(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isEmpty.value = false
            // Quan trọng: Xóa dữ liệu cũ ngay khi bắt đầu yêu cầu mới
            _weeklyNutritionData.value = null

            val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            Log.d(TAG, "fetchWeeklySummary: Bắt đầu gọi API cho ngày: $formattedDate")

            try {
                val result = foodLogRepository.getWeeklySummary(formattedDate)
                Log.d(TAG, "fetchWeeklySummary: Nhận được kết quả từ repository: $result")

                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "onSuccess: Tải dữ liệu thành công.")
                        // Kiểm tra dữ liệu có rỗng không
                        if (response.totalWeeklyCalories == 0.0 && response.mealSummaries.isEmpty()) {
                            Log.d(TAG, "onSuccess: Dữ liệu rỗng. Cập nhật trạng thái 'isEmpty'.")
                            _isEmpty.value = true
                        } else {
                            _weeklyNutritionData.value = response
                        }
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "onFailure: Tải dữ liệu thất bại.", exception)
                        _error.value = exception.message ?: "Có lỗi xảy ra khi tải dữ liệu"
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "fetchWeeklySummary: Đã xảy ra exception.", e)
                _error.value = e.message ?: "Có lỗi không mong muốn xảy ra"
            } finally {
                Log.d(TAG, "fetchWeeklySummary: Hoàn thành khối try-catch-finally.")
                _isLoading.value = false
            }
        }
    }

    fun refreshToCurrentDate() {
        val today = LocalDate.now()
        if (_selectedDate.value != today) {
            Log.d(TAG, "refreshToCurrentDate: Yêu cầu làm mới về ngày hôm nay.")
            _selectedDate.value = today
        } else {
            // Nếu đã ở ngày hôm nay, vẫn trigger gọi lại API để đảm bảo dữ liệu mới nhất
            Log.d(TAG, "refreshToCurrentDate: Đã ở ngày hôm nay, trigger fetch lại dữ liệu.")
            _selectedDate.value = today // Gán lại để trigger observer
        }
    }

    fun onPreviousWeekClicked() {
        _selectedDate.value = _selectedDate.value?.minusWeeks(1)
    }

    fun onNextWeekClicked() {
        _selectedDate.value = _selectedDate.value?.plusWeeks(1)
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    override fun onCleared() {
        super.onCleared()
        _selectedDate.removeObserver(dateObserver)
    }
}
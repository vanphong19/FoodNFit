package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.vanphong.foodnfit.model.WeeklyExerciseSummaryResponse
import com.vanphong.foodnfit.repository.WorkoutPlanRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WorkoutPlanViewModel : ViewModel() {
    private val TAG = "WorkoutPlanVM"
    // Giả sử bạn inject hoặc khởi tạo repository
    private val workoutPlanRepository = WorkoutPlanRepository()

    // --- State LiveData ---
    private val _weeklySummary = MutableLiveData<WeeklyExerciseSummaryResponse?>()
    val weeklySummary: LiveData<WeeklyExerciseSummaryResponse?> get() = _weeklySummary

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

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
        // Không cần gán giá trị ở đây, vì onResume của Fragment sẽ gọi refreshToCurrentDate
    }

    private fun fetchWeeklySummary(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isEmpty.value = false
            _weeklySummary.value = null // Xóa dữ liệu cũ ngay khi bắt đầu

            val formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            Log.d(TAG, "fetchWeeklySummary: Bắt đầu gọi API cho ngày: $formattedDate")

            try {
                // Giả sử repository trả về kotlin.Result
                val result = workoutPlanRepository.getWeeklySummary(formattedDate)
                Log.d(TAG, "fetchWeeklySummary: Nhận được kết quả từ repository: $result")

                result.fold(
                    onSuccess = { response ->
                        Log.d(TAG, "onSuccess: Tải dữ liệu thành công.")
                        // Kiểm tra dữ liệu có rỗng không (không có buổi tập nào)
                        if (response.totalTrainingSessions == 0) {
                            Log.d(TAG, "onSuccess: Dữ liệu rỗng. Cập nhật trạng thái 'isEmpty'.")
                            _isEmpty.value = true
                        } else {
                            _weeklySummary.value = response
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
            _selectedDate.value = today
        } else {
            // Nếu đã ở ngày hôm nay, vẫn trigger gọi lại API
            fetchWeeklySummary(today)
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
package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.WorkoutExerciseResponse
import com.vanphong.foodnfit.repository.WorkoutPlanRepository
import com.vanphong.foodnfit.util.CalendarUtils
import com.github.mikephil.charting.data.Entry // *** THÊM IMPORT ***
import com.vanphong.foodnfit.model.ReminderRequest
import com.vanphong.foodnfit.model.ReminderResponse
import com.vanphong.foodnfit.repository.ReminderRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ExerciseChartData(
    val entries: List<Entry>,
    val labels: List<String>
)

class ExerciseViewModel : ViewModel() {
    private val repository = WorkoutPlanRepository()

    private val reminderRepository = ReminderRepository()
    private val _addExerciseList = MutableLiveData<List<WorkoutExerciseResponse>>()
    val addExerciseList: LiveData<List<WorkoutExerciseResponse>> get() = _addExerciseList

    // *** THÊM MỚI: LiveData dành riêng cho biểu đồ ***
    private val _exerciseChartData = MutableLiveData<ExerciseChartData>()
    val exerciseChartData: LiveData<ExerciseChartData> get() = _exerciseChartData

    // Các LiveData khác không đổi
    private val _totalCaloriesBurnt = MutableLiveData(0.0)
    val totalCaloriesBurnt: LiveData<Double> get() = _totalCaloriesBurnt

    private val _planInfoString = MutableLiveData<String>()
    val planInfoString: LiveData<String> get() = _planInfoString

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _navigateExerciseList = MutableLiveData<Boolean>()
    val navigateExerciseList: LiveData<Boolean> get() = _navigateExerciseList

    private val _navigateExerciseInfo = MutableLiveData<Int?>()
    val navigateExerciseInfo: LiveData<Int?> get() = _navigateExerciseInfo
    private val _createResult = MutableLiveData<Result<ReminderResponse>>()
    val createResult: LiveData<Result<ReminderResponse>> get() = _createResult

    init {
        fetchDataForDate(CalendarUtils.selectedDate)
    }

    fun fetchDataForDate(date: LocalDate) {
        fetchDailyWorkoutPlan(date)
    }

    fun createReminder(request: ReminderRequest) {
        viewModelScope.launch {
            val result = reminderRepository.create(request)
            _createResult.value = result
        }
    }

    private fun fetchDailyWorkoutPlan(date: LocalDate) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.getWorkoutPlanByDate(date)

            result.onSuccess { data ->
                _addExerciseList.postValue(data.exerciseResponses)
                _totalCaloriesBurnt.postValue(data.totalCaloriesBurnt)
                val dateText = formatDateForInfo(date)
                _planInfoString.postValue("$dateText - ${data.exerciseCount} hoạt động")
                _errorMessage.value = null

                prepareChartData(data.exerciseResponses)

            }.onFailure { exception ->
                if (exception is HttpException && exception.code() == 404) {
                    _addExerciseList.postValue(emptyList())
                    _totalCaloriesBurnt.postValue(0.0)
                    val dateText = formatDateForInfo(date)
                    _planInfoString.postValue("$dateText - Không có hoạt động")
                    _errorMessage.value = null

                    _exerciseChartData.postValue(ExerciseChartData(emptyList(), emptyList()))
                } else {
                    // Xử lý các lỗi khác
                    _errorMessage.postValue(exception.message ?: "Đã có lỗi xảy ra")
                    _addExerciseList.postValue(emptyList())
                    _totalCaloriesBurnt.postValue(0.0)
                    _planInfoString.postValue("Không thể tải dữ liệu")
                    Log.e("lõi", "Lỗi API thực sự: ${exception.message}")

                    _exerciseChartData.postValue(ExerciseChartData(emptyList(), emptyList()))
                }
            }
            _isLoading.value = false
        }
    }

    /**
     * Hàm mới để chuẩn bị dữ liệu cho biểu đồ từ danh sách bài tập.
     */
    private fun prepareChartData(exercises: List<WorkoutExerciseResponse>) {
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        exercises.forEachIndexed { index, exercise ->
            entries.add(Entry(index.toFloat(), exercise.caloriesBurnt.toFloat()))
            labels.add(exercise.exerciseName)
        }

        _exerciseChartData.postValue(ExerciseChartData(entries, labels))
    }

    fun markExerciseAsCompleted(position: Int) {
        val currentList = _addExerciseList.value?.toMutableList()
        if (currentList != null && position >= 0 && position < currentList.size) {
            val item = currentList[position]
            val updatedItem = item.copy(isCompleted = true)
            currentList[position] = updatedItem
            _addExerciseList.value = currentList.toList()
        }
    }

    private fun formatDateForInfo(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("d 'tháng' M", Locale("vi", "VN")))
    }
    fun onClickNavigateExerciseInfo(exerciseId: Int) {
        _navigateExerciseInfo.value = exerciseId
    }

    fun completeNavigateExerciseInfo() {
        _navigateExerciseInfo.value = null
    }

    fun onClickNavigateExerciseList() {
        _navigateExerciseList.value = true
    }

    fun completeNavigateExerciseList() {
        _navigateExerciseList.value = false
    }
}
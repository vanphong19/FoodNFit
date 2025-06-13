package com.vanphong.foodnfit.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExerciseRepository()

    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl

    // Video URL lưu trong LiveData
    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> get() = _videoUrl

    // Thuộc tính trả về giá trị String cho binding
    val videoIdString: String?
        get() = _videoUrl.value

    // Các LiveData còn lại giữ nguyên
    private val _exerciseName = MutableLiveData<String>()
    val exerciseName: LiveData<String> = _exerciseName

    private val _muscleGroup = MutableLiveData<String>()
    val muscleGroup: LiveData<String> = _muscleGroup

    private val _difficulty = MutableLiveData<String>()
    val difficulty: LiveData<String> = _difficulty

    private val _sets = MutableLiveData<String?>()
    val sets: LiveData<String?> = _sets

    private val _reps = MutableLiveData<String?>()
    val reps: LiveData<String?> = _reps

    private val _restTime = MutableLiveData<String?>()
    val restTime: LiveData<String?> = _restTime

    private val _minute = MutableLiveData<String?>()
    val minute: LiveData<String?> = _minute

    private val _equipment = MutableLiveData<String>()
    val equipment: LiveData<String> = _equipment

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    private val _caloriesPerHour = MutableLiveData<String>()
    val caloriesPerHour: LiveData<String> = _caloriesPerHour

    private val _instructions = MutableLiveData<String>()
    val instructions: LiveData<String> = _instructions

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadExerciseInfo(id: Int) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val exerciseResponse = repository.getById(id)
                withContext(Dispatchers.Main) {
                    _exerciseName.value = exerciseResponse?.name
                    _description.value = exerciseResponse?.description
                    _videoUrl.value = exerciseResponse?.videoUrl
                    _imageUrl.value = exerciseResponse?.imageUrl
                    _difficulty.value = exerciseResponse?.difficultyLevel
                    _muscleGroup.value = exerciseResponse?.muscleGroup
                    _caloriesPerHour.value = exerciseResponse?.caloriesBurnt.toString()
                    _minute.value = exerciseResponse?.minutes?.toString()
                    _sets.value = exerciseResponse?.sets?.toString()
                    _reps.value = exerciseResponse?.reps?.toString()
                    _restTime.value = exerciseResponse?.restTimeSeconds?.toString()
                    _equipment.value = exerciseResponse?.equipmentRequired
                    _instructions.value = exerciseResponse?.note
                    _category.value = exerciseResponse?.type
                    _error.value = null
                    _loading.value = false

                    Log.d("video", videoIdString?:"")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Lỗi ${e.message}"
                    _loading.value = false
                }
            }
        }
    }
}

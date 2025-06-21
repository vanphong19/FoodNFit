package com.vanphong.foodnfit.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.ExerciseRequest
import com.vanphong.foodnfit.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExerciseRepository()
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

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

    private val _sets = MutableLiveData<Int?>()
    val sets: LiveData<Int?> = _sets

    private val _reps = MutableLiveData<String?>()
    val reps: LiveData<String?> = _reps

    private val _restTime = MutableLiveData<String?>()
    val restTime: LiveData<String?> = _restTime

    private val _minute = MutableLiveData<Int?>()
    val minute: LiveData<Int?> = _minute

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

    private val _createdExercise = MutableLiveData<Boolean>()
    val createdExercise: LiveData<Boolean> get() = _createdExercise
    private val _notify = MutableLiveData<String>()
    val notify: LiveData<String> get() = _notify

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
                    _minute.value = exerciseResponse?.minutes
                    _sets.value = exerciseResponse?.sets
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

    fun create() {
        val request = toRequest() ?: return

        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.createExercise(request)
                response.onSuccess {
                    _createdExercise.postValue(true)
                    _error.postValue(null)
                }.onFailure {
                    _createdExercise.postValue(false)
                    _error.postValue(context.getString(R.string.create_exercise_failed) + ": code ${it.message}")
                }
            } catch (e: Exception) {
                _createdExercise.postValue(false)
                _error.postValue(context.getString(R.string.create_food_failed) + ": ${e.message}")
            }
        }
    }


    fun update(foodId: Int){
        val request = toRequest() ?: return

        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.update(foodId, request)
                response.onSuccess {
                    _createdExercise.postValue(true)
                    _error.postValue(null)
                }.onFailure {
                    _createdExercise.postValue(false)
                    _error.postValue(context.getString(R.string.update_exercise_failed) + ": code ${it.message}")
                }
            } catch (e: Exception) {
                _createdExercise.postValue(false)
                _error.postValue(context.getString(R.string.update_exercise_failed) + ": ${e.message}")
            }
        }
    }
    fun remove(exerciseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.remove(exerciseId)
                result.onSuccess {
                    // message là chuỗi trả về từ server, ví dụ: "Exercise with id ... has been removed successfully."
                    _notify.postValue("removed")  // Hoặc có thể post message gốc nếu muốn
                }.onFailure { error ->
                    _notify.postValue("error:${error.message}")
                }
            } catch (e: Exception) {
                _notify.postValue("error:${e.message}")
            }
        }
    }


    private fun toRequest(): ExerciseRequest? {
        return try {
            ExerciseRequest(
                exerciseName = _exerciseName.value ?: "",
                description = _description.value ?: "",
                videoUrl = _videoUrl.value,
                imageUrl = _imageUrl.value,
                difficultyLevel = _difficulty.value ?: "",
                muscleGroup = _muscleGroup.value ?: "",
                caloriesBurnt = _caloriesPerHour.value?.toDoubleOrNull(),
                minutes = _minute.value,
                sets = _sets.value,
                reps = _reps.value?.toIntOrNull(),
                restTimeSeconds = _restTime.value?.toIntOrNull(),
                equipmentRequired = _equipment.value ?: "",
                note = _instructions.value ?: "",
                exerciseType = _category.value?: "",
                active = true
            )
        } catch (e: Exception) {
            _error.value = "Dữ liệu không hợp lệ: ${e.message}"
            null
        }
    }

}

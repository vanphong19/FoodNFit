package com.vanphong.foodnfit.admin.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.R
import com.vanphong.foodnfit.model.ExerciseRequest
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ExerciseViewModel(application: Application): AndroidViewModel(application) {
    private val repository = ExerciseRepository()
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    // Lưu URL ảnh sau khi upload thành công
    private val _imageUrl = MutableLiveData<String?>()
    val imageUrl: LiveData<String?> = _imageUrl

    // Kết quả tạo bài tập
    private val _exerciseCreated = MutableLiveData<Exercises?>()
    val exerciseCreated: LiveData<Exercises?> = _exerciseCreated

    // Lỗi chung (upload hoặc tạo bài tập)
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Video URL
    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> = _videoUrl

    // Exercise Name
    private val _exerciseName = MutableLiveData<String>()
    val exerciseName: LiveData<String> = _exerciseName

    // Muscle Group
    private val _muscleGroup = MutableLiveData<String>()
    val muscleGroup: LiveData<String> = _muscleGroup

    // Difficulty
    private val _difficulty = MutableLiveData<String>()
    val difficulty: LiveData<String> = _difficulty

    // Sets
    private val _sets = MutableLiveData<String?>()
    val sets: LiveData<String?> = _sets

    // Reps
    private val _reps = MutableLiveData<String?>()
    val reps: LiveData<String?> = _reps

    // Rest Time (seconds)
    private val _restTime = MutableLiveData<String?>()
    val restTime: LiveData<String?> = _restTime

    // Minute (duration)
    private val _minute = MutableLiveData<String?>()
    val minute: LiveData<String?> = _minute

    // Equipment Required
    private val _equipment = MutableLiveData<String>()
    val equipment: LiveData<String> = _equipment

    // Exercise Description
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    // Exercise Category / Type
    private val _category = MutableLiveData<String>()
    val category: LiveData<String> = _category

    // Calories burnt per hour
    private val _caloriesPerHour = MutableLiveData<String>()
    val caloriesPerHour: LiveData<String> = _caloriesPerHour

    // Instructions / Notes
    private val _instructions = MutableLiveData<String>()
    val instructions: LiveData<String> = _instructions

    // Các hàm cập nhật dữ liệu
    fun setImageUri(uri: String?) {
        _imageUrl.value = uri
    }

    fun setVideoUrl(url: String) {
        _videoUrl.value = url
    }

    fun setExerciseName(name: String) {
        _exerciseName.value = name
    }

    fun setMuscleGroup(muscleGroup: String) {
        _muscleGroup.value = muscleGroup
    }

    fun setDifficulty(difficulty: String) {
        _difficulty.value = difficulty
    }

    fun setSets(sets: String) {
        _sets.value = sets
    }

    fun setReps(reps: String) {
        _reps.value = reps
    }

    fun setRestTime(restTime: String) {
        _restTime.value = restTime
    }

    fun setMinute(minute: String) {
        _minute.value = minute
    }

    fun setEquipment(equipment: String) {
        _equipment.value = equipment
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setCaloriesPerHour(calories: String) {
        _caloriesPerHour.value = calories
    }

    fun setInstructions(instructions: String) {
        _instructions.value = instructions
    }

    private val _notify = MutableLiveData<String>()
    val notify: LiveData<String> get() = _notify
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // Hàm upload ảnh file lên server
        fun uploadImage(file: File) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = repository.uploadImageFromFile(file)
                    if (response.isSuccessful) {
                        val url = response.body()?.url
                        if (url != null) {
                            _imageUrl.postValue(url)  // Lưu URL ảnh public
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

    // Hàm tạo bài tập mới với dữ liệu đầy đủ (gồm URL ảnh)
    fun createExercise() {

        val request = setRequest()?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.createExercise(request)
                response.onSuccess {
                    _exerciseCreated.postValue(it)
                    _error.postValue(null)
                }
                    .onFailure {
                        _exerciseCreated.postValue(null)
                        _error.postValue("Tạo bài tập thất bại: code ${it.message}")
                    }
            } catch (e: Exception) {
                _exerciseCreated.postValue(null)
                _error.postValue("Tạo bài tập thất bại: ${e.message}")
            }
        }
    }

    fun updateExercise(exerciseId: Int){
        val request = setRequest()?: return

        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.update(exerciseId, request)
                response.onSuccess {
                    _exerciseCreated.postValue(it)
                    _error.postValue(null)
                }.onFailure {
                    _exerciseCreated.postValue(null)
                    _error.postValue(context.getString(R.string.update_exercise_failed) + ": code ${it.message}")
                }
            } catch (e: Exception) {
                _exerciseCreated.postValue(null)
                _error.postValue(context.getString(R.string.update_exercise_failed) + ": ${e.message}")
            }
        }
    }

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
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.value = "Lỗi ${e.message}"
                    _loading.value = false
                }
            }
        }
    }

    private fun setRequest(): ExerciseRequest? {
        val calories = _caloriesPerHour.value?.toFloatOrNull() ?: -1f
        val minutesInt = _minute.value?.toIntOrNull()
        val setsInt = _sets.value?.toIntOrNull()
        val repsInt = _reps.value?.toIntOrNull()
        val restTimeInt = _restTime.value?.toIntOrNull()
        val imgUrl = _imageUrl.value ?: ""

        // Kiểm tra trường bắt buộc
        val name = _exerciseName.value ?: ""
        val description = _description.value ?: ""
        val muscleGroup = _muscleGroup.value ?: ""
        val difficulty = _difficulty.value ?: ""
        val equipment = _equipment.value ?: ""
        val type = _category.value ?: ""
        val videoUrl = _videoUrl.value ?: ""

        if (imgUrl.isEmpty()) {
            _error.value = "Vui lòng upload ảnh trước khi lưu bài tập"
            return null
        }
        if (name.isBlank()) {
            _error.value = "Vui lòng nhập tên bài tập"
            return null
        }
        if (description.isBlank()) {
            _error.value = "Vui lòng nhập mô tả bài tập"
            return null
        }
        if (muscleGroup.isBlank()) {
            _error.value = "Vui lòng nhập nhóm cơ"
            return null
        }
        if (calories < 0f) {
            _error.value = "Vui lòng nhập calories hợp lệ"
            return null
        }
        if (difficulty.isBlank()) {
            _error.value = "Vui lòng nhập độ khó"
            return null
        }
        if (equipment.isBlank()) {
            _error.value = "Vui lòng nhập dụng cụ"
            return null
        }
        if (videoUrl.isBlank()) {
            _error.value = "Vui lòng nhập URL video"
            return null
        }

        return ExerciseRequest(
            exerciseName = name,
            description = description,
            videoUrl = videoUrl,
            imageUrl = imgUrl,
            difficultyLevel = difficulty,
            muscleGroup = muscleGroup,
            caloriesBurnt = calories.toDouble() ?: 0.0,
            minutes = minutesInt ?: 0,
            sets = setsInt ?: 0,
            reps = repsInt ?: 0,
            restTimeSeconds = restTimeInt,
            equipmentRequired = equipment,
            note = _instructions.value ?: "",
            exerciseType = type,
            active = true
        )
    }
}
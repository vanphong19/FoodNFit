package com.vanphong.foodnfit.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.*
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.repository.WorkoutExerciseRepository
import com.vanphong.foodnfit.repository.WorkoutPlanRepository
import kotlinx.coroutines.launch

// Các lớp Event và Enum này đã đúng, giữ nguyên
open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) null else {
            hasBeenHandled = true
            content
        }
    }
    fun peekContent(): T = content
}
enum class PlanCreationStatus { SUCCESS, ERROR, LOADING, IDLE }


class ListExerciseViewModel(private val repository: ExerciseRepository): ViewModel() {
    private val workoutPlanRepository = WorkoutPlanRepository()
    private val workoutExerciseRepository = WorkoutExerciseRepository()

    // Cấu trúc LiveData được giữ nguyên theo yêu cầu của bạn
    private val _listExercise = MutableLiveData<List<Exercises>>()
    val listExercises: LiveData<List<Exercises>> get() = _listExercise

    private val _selectedExercises = MutableLiveData<List<WorkoutExercises>>()
    val selectedExercises: LiveData<List<WorkoutExercises>> get() = _selectedExercises

    private val _planCreationStatus = MutableLiveData<Event<PlanCreationStatus>>()
    val planCreationStatus: LiveData<Event<PlanCreationStatus>> get() = _planCreationStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _search = MutableLiveData<String?>()
    val search: LiveData<String?> get() = _search

    private var currentPage = 0
    private var isLastPage = false
    private val pageSize = 10

    fun setListExercise(refresh: Boolean = false){
        if (_isLoading.value == true || (isLastPage && !refresh)) return

        viewModelScope.launch {
            _isLoading.value = true

            if (refresh) {
                currentPage = 0
                isLastPage = false
            }

            val result = repository.getAllExercises(
                search = _search.value ?: "", currentPage, pageSize)
            result.onSuccess {
                val newList = if (currentPage == 0) {
                    it.content
                } else {
                    (_listExercise.value ?: emptyList()) + it.content
                }
                _listExercise.value = newList
                isLastPage = it.number + 1 >= it.totalPages
                currentPage++
            }.onFailure {
                Log.e("ListExerciseViewModel", "Error loading more", it)
            }
            _isLoading.value = false
        }
    }

    fun addExercise(exercise: WorkoutExercises) {
        val currentList = _selectedExercises.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.exerciseId == exercise.exerciseId }
        if (index != -1) {
            currentList[index] = exercise
        } else {
            currentList.add(exercise)
        }
        _selectedExercises.value = currentList
    }

    fun removeExercise(exercise: WorkoutExercises) {
        val currentList = _selectedExercises.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.exerciseId == exercise.exerciseId }
        _selectedExercises.value = currentList
    }

    fun makePlan() {
        viewModelScope.launch {
            val exercisesToSave = _selectedExercises.value
            if (exercisesToSave.isNullOrEmpty()) {
                Log.d("MakePlan", "No exercises selected.")
                return@launch
            }

            _planCreationStatus.value = Event(PlanCreationStatus.LOADING)

            val totalCalories = exercisesToSave.sumOf { it.caloriesBurnt?.toDouble() ?: 0.0 }
            val exerciseCount = exercisesToSave.size
            val planRequest = WorkoutPlanRequest(exerciseCount, totalCalories)

            val planResult = workoutPlanRepository.createWorkoutPlan(planRequest)

            planResult.onSuccess { planResponse ->
                val planId = planResponse.id

                // SỬA LỖI QUAN TRỌNG: Loại bỏ `?: 0` để giữ lại giá trị null nguyên bản
                val exerciseRequests = exercisesToSave.map { workoutEx ->
                    WorkoutExerciseRequest(
                        exerciseId = workoutEx.exerciseId,
                        sets = workoutEx.sets,                 // Truyền thẳng giá trị, có thể là null
                        reps = workoutEx.reps,                 // Truyền thẳng giá trị, có thể là null
                        restTimeSecond = workoutEx.restTimeSeconds, // Truyền thẳng giá trị, có thể là null
                        caloriesBurnt = workoutEx.caloriesBurnt?.toDouble() ?: 0.0,
                        minutes = workoutEx.minute             // Truyền thẳng giá trị, có thể là null
                    )
                }
                val batchRequest = WorkoutExerciseBatchRequest(planId, exerciseRequests)

                // Gọi API với dữ liệu chính xác
                val saveExercisesResult = workoutExerciseRepository.saveAll(batchRequest)
                saveExercisesResult.onSuccess {
                    _planCreationStatus.value = Event(PlanCreationStatus.SUCCESS)
                    _selectedExercises.postValue(emptyList())
                }.onFailure {
                    Log.e("MakePlan", "Failed to save exercises", it)
                    _planCreationStatus.value = Event(PlanCreationStatus.ERROR)
                }

            }.onFailure {
                Log.e("MakePlan", "Failed to create plan", it)
                _planCreationStatus.value = Event(PlanCreationStatus.ERROR)
            }
        }
    }
}
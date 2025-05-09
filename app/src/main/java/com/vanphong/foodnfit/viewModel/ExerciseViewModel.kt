package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.WorkoutExercises

class ExerciseViewModel: ViewModel() {
    private val _addExerciseList = MutableLiveData<List<WorkoutExercises>>()
    val addExerciseList: LiveData<List<WorkoutExercises>> get() = _addExerciseList
    fun setAddExerciseList(data: List<WorkoutExercises>){
        _addExerciseList.value = data
    }
    private val _navigateExerciseList = MutableLiveData<Boolean>()
    val navigateExerciseList: LiveData<Boolean> get() = _navigateExerciseList
    private val _navigateExerciseInfo = MutableLiveData<Int?>()
    val navigateExerciseInfo: LiveData<Int?> = _navigateExerciseInfo

    fun onClickNavigateExerciseInfo(exerciseId: Int) {
        _navigateExerciseInfo.value = exerciseId
    }

    fun completeNavigateExerciseInfo() {
        _navigateExerciseInfo.value = null
    }
    fun onClickNavigateExerciseList(){
        _navigateExerciseList.value = true
    }
    fun completeNavigateExerciseList(){
        _navigateExerciseList.value = false
    }
}
package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.WorkoutPlan

class WorkoutPlanViewModel: ViewModel() {
    private val _workoutList = MutableLiveData<List<WorkoutPlan>>()
    val workoutList: LiveData<List<WorkoutPlan>> get() = _workoutList
    fun setWorkoutList(data: List<WorkoutPlan>){
        _workoutList.value = data
    }
}
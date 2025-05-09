package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.WorkoutExercises

class ListExerciseViewModel: ViewModel() {
    private val _listExercise = MutableLiveData<List<Exercises>>()
    val listExercises: LiveData<List<Exercises>> get() = _listExercise
    private val _selectedExercises = MutableLiveData<List<WorkoutExercises>>()
    val selectedExercises: LiveData<List<WorkoutExercises>> get() = _selectedExercises
    fun setListExercise(data: List<Exercises>){
        _listExercise.value = data
    }
    fun setSelectedExercises(data: List<WorkoutExercises>){
        _selectedExercises.value = data
    }
}
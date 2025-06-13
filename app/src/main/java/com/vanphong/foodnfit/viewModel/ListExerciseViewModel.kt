package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.Model.Exercises
import com.vanphong.foodnfit.Model.WorkoutExercises
import com.vanphong.foodnfit.repository.ExerciseRepository
import kotlinx.coroutines.launch

class ListExerciseViewModel(private val repository: ExerciseRepository): ViewModel() {
    private val _listExercise = MutableLiveData<List<Exercises>>()
    val listExercises: LiveData<List<Exercises>> get() = _listExercise
    private val _selectedExercises = MutableLiveData<List<WorkoutExercises>>()
    val selectedExercises: LiveData<List<WorkoutExercises>> get() = _selectedExercises
    fun setListExercise(){
        viewModelScope.launch {
            val list = repository.getAll()
            _listExercise.postValue(list?: emptyList())
        }
    }
    fun setSelectedExercises(data: List<WorkoutExercises>){
        _selectedExercises.value = data
    }
}
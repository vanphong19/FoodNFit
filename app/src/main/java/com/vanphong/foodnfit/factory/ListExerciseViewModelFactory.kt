package com.vanphong.foodnfit.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vanphong.foodnfit.repository.ExerciseRepository
import com.vanphong.foodnfit.viewModel.ListExerciseViewModel

class ListExerciseViewModelFactory(private val repository: ExerciseRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ListExerciseViewModel::class.java)){
            @Suppress
            return ListExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}
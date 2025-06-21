package com.vanphong.foodnfit.admin.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanphong.foodnfit.model.Exercises
import com.vanphong.foodnfit.model.WorkoutExercises
import com.vanphong.foodnfit.repository.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseListViewModel(private val repository: ExerciseRepository): ViewModel() {
    private val _listExercise = MutableLiveData<List<Exercises>>()
    val listExercises: LiveData<List<Exercises>> get() = _listExercise
    private val _selectedExercises = MutableLiveData<List<WorkoutExercises>>()
    val selectedExercises: LiveData<List<WorkoutExercises>> get() = _selectedExercises
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _search = MutableLiveData<String?>()
    val search: LiveData<String?> get() = _search
    private var currentPage = 0
    private var isLastPage = false
    private val pageSize = 10
    fun setListExercise(){
        if (_isLoading.value == true || isLastPage) return

        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getAllExercises(
                search = _search.value ?: "", currentPage, pageSize)
            result.onSuccess {
                val currentList = _listExercise.value ?: emptyList()
                _listExercise.value = currentList + it.content

                // cập nhật trạng thái phân trang
                isLastPage = it.number + 1 >= it.totalPages
                currentPage++
            }.onFailure {
                Log.e("FoodItem", "Error loading more", it)
            }
            _isLoading.value = false
        }
    }
    fun setSelectedExercises(data: List<WorkoutExercises>){
        _selectedExercises.value = data
    }
}
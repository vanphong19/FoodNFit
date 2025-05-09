package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.DailyCalories

class CaloriesStatisticViewModel: ViewModel() {
    private val _dailyCaloriesList = MutableLiveData<List<DailyCalories>>()
    val dailyCaloriesList: LiveData<List<DailyCalories>> get() = _dailyCaloriesList

    fun setDailyCaloriesList(data: List<DailyCalories>){
        _dailyCaloriesList.value = data
    }
}
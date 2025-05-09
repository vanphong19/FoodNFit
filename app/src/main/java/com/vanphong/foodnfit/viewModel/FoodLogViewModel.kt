package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.FoodLog

class FoodLogViewModel: ViewModel() {
    private val _foodLog = MutableLiveData<FoodLog>()
    val foodLog: LiveData<FoodLog> get() = _foodLog
    fun setFoodLog(foodLog: FoodLog){
        _foodLog.value = foodLog
    }
}
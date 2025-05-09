package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.UserProfiles

class StatisticViewModel: ViewModel() {
    private val _weightData = MutableLiveData<List<UserProfiles>>()
    val weightData: LiveData<List<UserProfiles>> = _weightData

    fun setWeight(data: List<UserProfiles>){
        _weightData.value = data
    }
}
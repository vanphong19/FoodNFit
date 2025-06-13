package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.UserProfiles

class ProfilesViewModel: ViewModel() {
    private val _weightHistories = MutableLiveData<List<UserProfiles>>()
    val weightHistories: LiveData<List<UserProfiles>> get() = _weightHistories
    fun setWeightHistories(data: List<UserProfiles>){
        _weightHistories.value = data
    }
}
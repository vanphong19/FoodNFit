package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonViewModel: ViewModel() {
    private val _navigateSetting = MutableLiveData<Boolean>()
    val navigateSetting: LiveData<Boolean> get() = _navigateSetting
    fun onClickNavigateSetting(){
        _navigateSetting.value = true
    }
    fun completeNavigateSetting(){
        _navigateSetting.value = false
    }
}
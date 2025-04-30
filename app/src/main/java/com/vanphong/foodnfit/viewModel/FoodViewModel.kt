package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.FoodItem

class FoodViewModel: ViewModel (){
    private val _allFoods = MutableLiveData<List<FoodItem>>()
    val allFoods: LiveData<List<FoodItem>> = _allFoods

    private val _navigateFoodDetail = MutableLiveData<Boolean>()
    val navigationFoodDetail: LiveData<Boolean> get() = _navigateFoodDetail

    fun setAllFoods(data: List<FoodItem>){
        _allFoods.value = data
    }

    fun onClickNavigateFoodDetail(){
        _navigateFoodDetail.value = true
    }

    fun onNavigationComplete() {
        _navigateFoodDetail.value = false
    }
}
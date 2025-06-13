package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.Model.SelectedFoodItem

class FoodViewModel: ViewModel (){
    private val _allFoods = MutableLiveData<List<FoodItem>>()
    val allFoods: LiveData<List<FoodItem>> = _allFoods
    private val _selectedMeals = MutableLiveData<MutableList<SelectedFoodItem>>(mutableListOf())
    val selectedMeals: LiveData<List<SelectedFoodItem>> get() = _selectedMeals.map { it.toList() }
    private val _mealCount = MutableLiveData<Int>()
    val mealCount: LiveData<Int> get() = _mealCount


    private val _navigateFoodDetail = MutableLiveData<Boolean>()
    val navigationFoodDetail: LiveData<Boolean> get() = _navigateFoodDetail
    private val _navigateChooseFood = MutableLiveData<Boolean>()
    val navigationChooseFood: LiveData<Boolean> get() = _navigateChooseFood

    fun setAllFoods(data: List<FoodItem>){
        _allFoods.value = data
    }

    fun addFoodToMeal(foodItem: FoodItem) {
        val currentList = _selectedMeals.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.foodItem.id == foodItem.id }

        if (index >= 0) {
            val existing = currentList[index]
            val updated = existing.copy(quantity = existing.quantity + 1)
            currentList[index] = updated
        } else {
            currentList.add(SelectedFoodItem(foodItem))
        }

        _selectedMeals.value = currentList
        setMealCount()
    }

    fun removeFoodFromMeal(foodItem: FoodItem) {
        val currentList = _selectedMeals.value?.toMutableList() ?: mutableListOf()
        val index = currentList.indexOfFirst { it.foodItem.id == foodItem.id }

        if (index >= 0) {
            val existing = currentList[index]
            if (existing.quantity > 1) {
                val updated = existing.copy(quantity = existing.quantity - 1)
                currentList[index] = updated
            } else {
                currentList.removeAt(index)
            }
            _selectedMeals.value = currentList
        }
        setMealCount()
    }

    private fun setMealCount(){
        _mealCount.value = _selectedMeals.value?.size?:0
    }

    fun onClickNavigateFoodDetail(){
        _navigateFoodDetail.value = true
    }

    fun onNavigationComplete() {
        _navigateFoodDetail.value = false
    }
    fun onClickNavigateChooseFood(){
        _navigateChooseFood.value = true
    }

    fun onChooseFoodComplete() {
        _navigateChooseFood.value = false
    }
}
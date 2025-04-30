package com.vanphong.foodnfit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vanphong.foodnfit.Model.FoodItem
import com.vanphong.foodnfit.Model.Ingredient

class FoodDetailViewModel: ViewModel() {
    private val _ingredients =MutableLiveData<List<Ingredient>>()
    val ingredients: LiveData<List<Ingredient>> = _ingredients

    private val _foodItem = MutableLiveData<FoodItem>()
    val foodItem: LiveData<FoodItem> = _foodItem

    private val _recipeSteps = MutableLiveData<List<String>>()
    val recipeSteps: LiveData<List<String>> = _recipeSteps
    fun setIngredients(data: List<Ingredient>){
        _ingredients.value = data
    }
    fun setFoodItem(foodItem: FoodItem){
        _foodItem.value = foodItem
    }
    fun setRecipeSteps(recipe: String){
        val steps = recipe.split("(?<=\\.)".toRegex()).map { it.trim() }
        _recipeSteps.value = steps
    }
}
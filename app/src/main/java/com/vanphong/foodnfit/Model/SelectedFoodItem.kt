package com.vanphong.foodnfit.Model
import com.vanphong.foodnfit.util.parseServingSize
data class SelectedFoodItem (
    val foodItem: FoodItem,
    var quantity: Int = 1
){
    val totalCalories: Float
        get() = (foodItem.calories ?: 0f) * quantity
    val totalServing: String
        get() {
            val (value, unit) = parseServingSize(foodItem.servingSize?:"0g")
            val total = value * quantity
            return "${total}${unit}"
        }
}
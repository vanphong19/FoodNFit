package com.vanphong.foodnfit.model

// Thay thế class SelectedFoodItem hiện có bằng class này
// trong file SelectedFoodItem.kt

data class SelectedFoodItem(
    val foodItem: FoodItemResponse,
    var quantity: Int = 1
) {
    // ***PHÉP TÍNH "CẤP SỐ CỘNG" XẢY RA Ở ĐÂY***
    // Thuộc tính này sẽ luôn trả về giá trị mới nhất.
    val totalCalories: Double
        get() = foodItem.calories * quantity // calories (cho 1 phần) * số lượng phần

    val totalProtein: Double
        get() = foodItem.protein * quantity

    val totalFat: Double
        get() = foodItem.fat * quantity

    val totalCarbs: Double
        get() = foodItem.carbs * quantity

    val totalServing: String
        get() = "$quantity serving(s)"
}
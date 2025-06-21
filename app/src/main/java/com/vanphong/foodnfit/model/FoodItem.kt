package com.vanphong.foodnfit.model

data class FoodItem (
    val id: Int,
    val name: String?,
    val calories: Float?,
    val protein: Float?,
    val carbs: Float?,
    val fat: Float?,
    val imageUrl: String?,
    val servingSize: String?,
    val recipe: String?,
    val foodTypeId: Int,
    val isActive: Boolean
    )

data class FoodItemResponse(
    val id: Int,
    val nameEn: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val imageUrl: String,
    val servingSizeEn: String,
    val recipeEn: String,
    val nameVi: String,
    val recipeVi: String,
    val servingSizeVi: String,
    val foodTypeId: Int,
    val active: Boolean,
    val ingredientsEn: String,
    val createdDate: String
)

data class FoodItemRequest(
    val nameEn: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val imageUrl: String,
    val servingSizeEn: String,
    val recipeEn: String,
    val foodTypeId: Int,
    val ingredientsEn: String,
)

data class FoodInfo(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float,
    val confidence: Float,
    val description: String
)
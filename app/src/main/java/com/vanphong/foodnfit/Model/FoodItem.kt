package com.vanphong.foodnfit.Model

import java.util.UUID

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
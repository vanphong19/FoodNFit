package com.vanphong.foodnfit.model

class WaterIntake {
}

data class WaterIntakeResponse(
    val id: Int,
    val userId: String,
    val cups: Int,
    val date: String,
    val updatedAt: String
)
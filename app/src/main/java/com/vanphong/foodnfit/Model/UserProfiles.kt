package com.vanphong.foodnfit.Model

import java.time.LocalDate

data class UserProfiles (
    val id: String,
    val userId: String,
    val height: Float,
    val weight: Float,
    val tdee: Float,
    val goal: String,
    val date: LocalDate
)
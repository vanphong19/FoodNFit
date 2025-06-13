package com.vanphong.foodnfit.Model

import java.time.LocalDate
import java.util.UUID

data class WorkoutPlan (
    val id: Int,
    val userId: UUID,
    val exerciseCount: Int,
    val date: LocalDate,
    val caloriesBurnt: Float
)
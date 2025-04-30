package com.vanphong.foodnfit.Model

import java.util.UUID

data class WorkoutPlan (
    val id: Int,
    val userId: UUID,
    val exerciseCount: Int
)
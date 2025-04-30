package com.vanphong.foodnfit.Model

import java.util.Date
import java.util.UUID

data class StepsTracking (
    val id: Int,
    val userId: UUID,
    val date: Date,
    val stepCount: Int
)
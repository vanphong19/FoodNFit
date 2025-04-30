package com.vanphong.foodnfit.Model

import java.time.LocalDateTime

data class WorkoutExercises (
    val id: Int,
    val exerciseId: Int,
    val sets: Int,
    val reps: Int,
    val restTimeSeconds: Int,
    val startTime: LocalDateTime?,
    val endTimeL: LocalDateTime?,
    val planId: Int
)
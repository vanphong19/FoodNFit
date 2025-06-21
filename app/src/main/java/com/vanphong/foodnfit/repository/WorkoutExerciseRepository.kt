package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.WorkoutExerciseBatchRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCallString
import com.vanphong.foodnfit.util.safeCallUnit

class WorkoutExerciseRepository {
    private val api = RetrofitClient.workoutExerciseService
    suspend fun saveAll(request: WorkoutExerciseBatchRequest): Result<Unit> = safeCallUnit {
        api.saveAll(request)
    }

    suspend fun deleteExercise(id: Int): Result<String> = safeCallString {
        api.deleteExercise(id)
    }
    suspend fun completeExercise(id: Int): Result<String> = safeCallString {
        api.completeExercise(id)
    }
}
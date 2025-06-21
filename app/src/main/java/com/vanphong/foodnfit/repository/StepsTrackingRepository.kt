package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.HourlyStepSummary
import com.vanphong.foodnfit.model.StepSummary
import com.vanphong.foodnfit.model.StepsTracking
import com.vanphong.foodnfit.model.StepsTrackingRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

class StepsTrackingRepository {
    private val api = RetrofitClient.stepsTrackingService
    suspend fun saveSteps(request: StepsTrackingRequest): Result<StepsTracking> = safeCall {
        api.saveSteps(request)
    }
    suspend fun getAll(): Result<List<StepsTracking>> = safeCall {
        api.getAll()
    }
    suspend fun getTodaySummary(): Result<StepSummary> = safeCall {
        api.getTodaySummary()
    }
    suspend fun getHourlySummary(): Result<List<HourlyStepSummary>> = safeCall {
        api.getHourlySummary()
    }
}
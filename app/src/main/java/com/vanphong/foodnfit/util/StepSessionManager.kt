package com.vanphong.foodnfit.util

import com.vanphong.foodnfit.Model.StepsTrackingRequest
import com.vanphong.foodnfit.repository.StepsTrackingRepository
import java.time.LocalDateTime

class StepSessionManager(
    private val repository: StepsTrackingRepository,
) {
    private var initialStepCount = -1
    private var sessionStarted = false
    private var stepsInSession = 0
    private var startTime: LocalDateTime = LocalDateTime.now()
    private var lastStepTime: Long = 0L

    fun onNewStep(totalSteps: Int) {
        val nowMillis = System.currentTimeMillis()
        val nowTime = LocalDateTime.now()

        if (initialStepCount == -1) {
            initialStepCount = totalSteps
            startTime = nowTime
            sessionStarted = true
        }

        stepsInSession = totalSteps - initialStepCount
        lastStepTime = nowMillis
    }

    suspend fun checkAndEndSessionIfNeeded() {
        val nowMillis = System.currentTimeMillis()
        if (sessionStarted && nowMillis - lastStepTime > 10000) {
            val request = StepsTrackingRequest(
                stepCount = stepsInSession,
                startTime = startTime,
                endTime = LocalDateTime.now()
            )

            try {
                repository.saveSteps(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            resetSession()
        }
    }

    private fun resetSession() {
        initialStepCount = -1
        sessionStarted = false
        stepsInSession = 0
    }
}

package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.ReminderRequest
import com.vanphong.foodnfit.model.ReminderResponse
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

class ReminderRepository {
    private val api = RetrofitClient.reminderService
    suspend fun create(request: ReminderRequest): Result<ReminderResponse> = safeCall {
        api.create(request)
    }
    suspend fun getAll(): Result<List<ReminderResponse>> = safeCall {
        api.getAll()
    }
    suspend fun getById(id: Int): Result<ReminderResponse> = safeCall {
        api.getById(id)
    }
}
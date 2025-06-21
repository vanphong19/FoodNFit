package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.model.FcmTokenRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

class FcmTokenRepository {
    private val fcmTokenApi = RetrofitClient.fcmTokenService

    suspend fun saveToken(request: FcmTokenRequest): Result<String> = safeCall {
        fcmTokenApi.saveToken(request)
    }

}
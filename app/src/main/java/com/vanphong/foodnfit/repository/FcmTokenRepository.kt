package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.Model.FcmTokenRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FcmTokenRepository {
    private val fcmTokenApi = RetrofitClient.fcmTokenService

    suspend fun saveToken(request: FcmTokenRequest): Result<String> = safeCall {
        fcmTokenApi.saveToken(request)
    }

}
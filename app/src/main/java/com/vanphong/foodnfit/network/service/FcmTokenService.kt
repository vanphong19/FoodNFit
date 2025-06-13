package com.vanphong.foodnfit.network.service

import com.vanphong.foodnfit.Model.FcmTokenRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface FcmTokenService {
    @POST("fcm/save-token")
    suspend fun saveToken(@Body request: FcmTokenRequest?): Response<String>
}

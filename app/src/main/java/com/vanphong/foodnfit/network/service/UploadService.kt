package com.vanphong.foodnfit.network.service

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class UploadResponse(
    val url: String // giả sử server trả về url ảnh trong trường này
)

interface UploadService {
    @Multipart
    @POST("upload/image") // endpoint upload ảnh của backend bạn
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<UploadResponse>
}
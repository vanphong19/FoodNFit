package com.vanphong.foodnfit.authenticator

import android.content.Context
import android.util.Log
import com.vanphong.foodnfit.Model.RefreshTokenRequest
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val context: Context
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null) ?: return null

        // Gọi API refresh token đồng bộ
        val newTokens = runBlocking {
            try {
                val authRepository = AuthRepository(RetrofitClient.authService)
                val result = authRepository.refreshToken(RefreshTokenRequest(refreshToken))
                result.getOrNull()
            } catch (e: Exception) {
                null
            }
        } ?: return null

        // Lưu token mới
        prefs.edit()
            .putString("access_token", newTokens.accessToken)
            .putString("refresh_token", newTokens.refreshToken)
            .apply()

        Log.d("TokenAuthenticator", "New access token: ${newTokens.accessToken}")

        // Tạo request mới với access token mới
        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var res = response.priorResponse
        var count = 1
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}
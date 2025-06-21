package com.vanphong.foodnfit.authenticator

import android.content.Context
import android.util.Log
import com.vanphong.foodnfit.model.RefreshTokenRequest
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
        // Chỉ retry tối đa 2 lần
        if (responseCount(response) >= 2) {
            Log.w("TokenAuthenticator", "Max retry attempts reached")
            return null
        }

        // Chỉ xử lý 401 (token expired), không xử lý 500/502/503
        if (response.code != 401) {
            Log.d("TokenAuthenticator", "Not a 401 error (${response.code}), skip token refresh")
            return null
        }

        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null)

        if (refreshToken.isNullOrEmpty()) {
            Log.w("TokenAuthenticator", "No refresh token available")
            return null
        }

        // Gọi API refresh token với retry
        val newTokens = runBlocking {
            try {
                val authRepository = AuthRepository(RetrofitClient.authService)
                Log.d("TokenAuthenticator", "Attempting to refresh token...")

                val result = authRepository.refreshToken(RefreshTokenRequest(refreshToken))
                val tokens = result.getOrNull()

                if (tokens != null) {
                    Log.d("TokenAuthenticator", "Token refresh successful")
                } else {
                    Log.e("TokenAuthenticator", "Token refresh failed: ${result.exceptionOrNull()?.message}")
                }

                tokens
            } catch (e: Exception) {
                Log.e("TokenAuthenticator", "Token refresh exception: ${e.message}")
                null
            }
        }

        return if (newTokens != null) {
            // Lưu token mới
            prefs.edit()
                .putString("access_token", newTokens.accessToken)
                .putString("refresh_token", newTokens.refreshToken)
                .apply()

            Log.d("TokenAuthenticator", "Tokens updated, retrying request")

            // Tạo request mới với access token mới
            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        } else {
            Log.w("TokenAuthenticator", "Failed to refresh token")
            null
        }
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
package com.vanphong.foodnfit

import android.app.Application
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.repository.AuthRepository
import com.vanphong.foodnfit.util.LogoutEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication:Application() {
    lateinit var authRepository: AuthRepository

    override fun onCreate() {
        super.onCreate()

        RetrofitClient.init(applicationContext) {
            CoroutineScope(Dispatchers.Main).launch {
                LogoutEventBus.logoutFlow.emit(Unit)
            }
        }

        authRepository = AuthRepository(RetrofitClient.authService)
    }
}
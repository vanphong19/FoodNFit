package com.vanphong.foodnfit.util

import kotlinx.coroutines.flow.MutableSharedFlow

object LogoutEventBus {
    val logoutFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}
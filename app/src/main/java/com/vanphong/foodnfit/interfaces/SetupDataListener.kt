package com.vanphong.foodnfit.interfaces

import com.vanphong.foodnfit.model.UserSetupInfo

interface SetupDataListener {
    fun onDataUpdated(data: UserSetupInfo)
}
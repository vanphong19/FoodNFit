package com.vanphong.foodnfit.repository

import com.vanphong.foodnfit.Model.PageResponse
import com.vanphong.foodnfit.Model.User
import com.vanphong.foodnfit.Model.UserRequest
import com.vanphong.foodnfit.Model.UserResponse
import com.vanphong.foodnfit.Model.UserResponseById
import com.vanphong.foodnfit.network.RetrofitClient
import com.vanphong.foodnfit.util.safeCall
import com.vanphong.foodnfit.util.safeCallString

class UserRepository {

    private val userService = RetrofitClient.userService

    suspend fun countUsers(): Result<Long> {
        return safeCall {
            userService.countUser()
        }
    }

    suspend fun countUsersThisMonth(): Result<Long> {
        return safeCall {
            userService.getCountUserThisMonth()
        }
    }

    suspend fun getAllUsers(
        search: String? = "",
        gender: Boolean? = null,
        block: Boolean? = null,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "id",
        sortDir: String = "asc"
    ): Result<PageResponse<UserResponse>> {
        return safeCall {
            userService.getAllUsers(search, gender, block, page, size, sortBy, sortDir)
        }
    }

    suspend fun getUserById(id: String): Result<UserResponseById> = safeCall {
        userService.getUserById(id)
    }

    suspend fun updateUser(id: String, request: UserRequest): Result<UserResponse> = safeCall {
        userService.updateUser(id, request)
    }

    suspend fun deleteUser(id: String): Result<UserResponse> = safeCall {
        userService.deleteUser(id)
    }

    suspend fun lockUser(id: String): Result<UserResponse> = safeCall {
        userService.lockUser(id)
    }

    suspend fun countUsersLastMonth(): Result<Long> = safeCall {
        userService.countUsersLastMonth()
    }
}

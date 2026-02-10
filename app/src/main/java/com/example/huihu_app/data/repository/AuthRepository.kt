package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CurrentUser
import com.example.huihu_app.data.model.UserNamePasswordAuthentication
import com.example.huihu_app.data.source.AuthSource
import com.example.huihu_app.state.AuthState

class AuthRepository(
    val authSource: AuthSource,
) {
    suspend fun login(userName: String, password: String): ApiResponse<CurrentUser> =
        runCatching {
            authSource.login(UserNamePasswordAuthentication(userName, password))
        }.getOrElse {
            return ApiResponse.from(it)
        }
}
package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.AuthToken
import com.example.huihu_app.data.model.CurrentUser
import com.example.huihu_app.data.model.RegisterRequest
import com.example.huihu_app.data.model.UserNamePasswordAuthentication
import com.example.huihu_app.data.source.AuthSource

class AuthRepository(
    val authSource: AuthSource,
) {
    suspend fun login(userName: String, password: String): ApiResponse<AuthToken> =
        runCatching {
            authSource.login(UserNamePasswordAuthentication(userName, password))
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun register(email: String, userName: String, password: String): ApiResponse<AuthToken> =
        runCatching {
            authSource.register(RegisterRequest(email = email, username = userName, password = password))
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun me(token: String): ApiResponse<CurrentUser> =
        runCatching {
            authSource.me("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }
}

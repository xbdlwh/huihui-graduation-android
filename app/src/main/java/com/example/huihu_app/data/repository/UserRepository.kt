package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.UserProfile
import com.example.huihu_app.data.model.UserProfileRequest
import com.example.huihu_app.data.source.UserSource

class UserRepository(
    val userSource: UserSource,
) {
    suspend fun getProfile(token: String): ApiResponse<UserProfile> =
        runCatching {
            userSource.getProfile("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun updateProfile(
        token: String,
        heightCm: Double? = null,
        weightKg: Double? = null,
        birthDate: String? = null,
        gender: String? = null
    ): ApiResponse<UserProfile> = runCatching {
        userSource.updateProfile(
            token = "Bearer $token",
            request = UserProfileRequest(
                height_cm = heightCm,
                weight_kg = weightKg,
                birth_date = birthDate,
                gender = gender
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }
}
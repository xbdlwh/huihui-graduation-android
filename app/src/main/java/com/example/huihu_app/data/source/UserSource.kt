package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.UserProfile
import com.example.huihu_app.data.model.UserProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserSource {
    @GET("/user/profile")
    suspend fun getProfile(@Header("Authorization") token: String): ApiResponse<UserProfile>

    @POST("/user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UserProfileRequest
    ): ApiResponse<UserProfile>
}
package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CurrentUser
import com.example.huihu_app.data.model.UserNamePasswordAuthentication
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthSource {
    @POST("/auth/login")
    suspend fun login(@Body request: UserNamePasswordAuthentication): ApiResponse<CurrentUser>
}
package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CalorieGoal
import com.example.huihu_app.data.model.CalorieGoalCreateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CalorieGoalSource {
    @GET("/user/calorie-goal/")
    suspend fun getCalorieGoal(@Header("Authorization") token: String): ApiResponse<CalorieGoal?>

    @POST("/user/calorie-goal/")
    suspend fun createCalorieGoal(
        @Header("Authorization") token: String,
        @Body request: CalorieGoalCreateRequest
    ): ApiResponse<CalorieGoal>
}
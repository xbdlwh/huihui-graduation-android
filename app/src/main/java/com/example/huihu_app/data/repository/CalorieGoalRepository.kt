package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CalorieGoal
import com.example.huihu_app.data.model.CalorieGoalCreateRequest
import com.example.huihu_app.data.source.CalorieGoalSource

class CalorieGoalRepository(
    val calorieGoalSource: CalorieGoalSource
) {
    suspend fun getCalorieGoal(token: String): ApiResponse<CalorieGoal?> =
        runCatching {
            calorieGoalSource.getCalorieGoal("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun createCalorieGoal(
        token: String,
        dailyCalorieGoal: Double,
        effectiveFrom: String
    ): ApiResponse<CalorieGoal> = runCatching {
        calorieGoalSource.createCalorieGoal(
            token = "Bearer $token",
            request = CalorieGoalCreateRequest(
                daily_calorie_goal = dailyCalorieGoal,
                effective_from = effectiveFrom
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }
}
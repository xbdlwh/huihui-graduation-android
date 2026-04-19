package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CalorieGoal(
    val id: Int,
    val user_id: Int,
    val daily_calorie_goal: Double,
    val effective_from: String,
    val effective_to: String? = null
)

@Serializable
data class CalorieGoalCreateRequest(
    val daily_calorie_goal: Double,
    val effective_from: String
)
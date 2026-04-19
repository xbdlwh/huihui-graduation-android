package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MealRecord(
    val id: Int,
    val user_id: Int,
    val meal_type: String,
    val source_type: String,
    val total_calories: Double,
    val note: String? = null,
    val created_at: String
)

@Serializable
data class CreateMealRecordRequest(
    val food_id: Int,
    val meal_type: String
)
package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseRecord(
    val id: Int,
    val user_id: Int,
    val exercise_type_id: Int,
    val exercise_name_snapshot: String,
    val met_value_snapshot: Double,
    val duration_minutes: Int,
    val body_weight_kg: Double,
    val calories_burned: Double,
    val occurred_at: String,
    val created_at: String
)

@Serializable
data class CreateExerciseRecordRequest(
    val exercise_type_id: Int,
    val duration_minutes: Int,
    val body_weight_kg: Double,
    val occurred_at: String
)

package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseType(
    val id: Int,
    val name: String,
    val met_value: Double
)

@Serializable
data class ExerciseTypeCreateRequest(
    val name: String,
    val met_value: Double
)

@Serializable
data class ExerciseTypeUpdateRequest(
    val id: Int,
    val name: String,
    val met_value: Double
)

package com.example.huihu_app.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UserProfile(
    val id: Int,
    val user_id: Int,
    val height_cm: Double,
    val weight_kg: Double,
    val birth_date: String,
    val gender: String
)

@Serializable
data class UserProfileRequest(
    val height_cm: Double? = null,
    val weight_kg: Double? = null,
    val birth_date: String? = null,
    val gender: String? = null
)
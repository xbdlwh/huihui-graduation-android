package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodComment(
    val id: Int,
    val food_id: Int,
    val user_id: Int,
    val content: String,
    val create_time: String,
    val thumb_count: Int,
    val thumbed: Boolean = false
)

@Serializable
data class CreateCommentRequest(
    val content: String
)

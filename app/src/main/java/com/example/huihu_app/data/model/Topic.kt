package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val images: List<String>?,
    val create_at: String,
    val user_info: TopicUserInfo? = null,
    val comment_count: Int = 0,
    val like_count: Int = 0,
    val liked: Boolean = false
)

@Serializable
data class TopicUserInfo(
    val id: Int,
    val name: String,
    val email: String
)

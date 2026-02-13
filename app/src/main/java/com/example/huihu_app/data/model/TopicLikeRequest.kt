package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TopicLikeRequest(
    val topic_id: Int,
    val like: Boolean
)

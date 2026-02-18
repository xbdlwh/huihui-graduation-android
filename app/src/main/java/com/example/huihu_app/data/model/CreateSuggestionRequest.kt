package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSuggestionRequest(
    val content: String,
    val images: List<String>,
    val type: String,
    val food_id: Int? = null,
    val restaurant_id: Int? = null
)

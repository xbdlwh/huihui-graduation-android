package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@Serializable
data class LikedFood(
    val id: Int,
    val restaurant_id: Int,
    val name: String,
    val description: String,
    val image: String,
    val restaurant: SuggestionRestaurant? = null
)

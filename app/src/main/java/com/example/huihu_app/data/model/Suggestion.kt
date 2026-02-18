package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@Serializable
data class Suggestion(
    val id: Int,
    val content: String,
    val type: String,
    val status: String,
    val created_at: String,
    val food: SuggestionFood? = null,
    val restaurant: SuggestionRestaurant? = null
)

@JsonIgnoreUnknownKeys
@Serializable
data class SuggestionFood(
    val id: Int,
    val name: String,
    val image: String? = null
)

@JsonIgnoreUnknownKeys
@Serializable
data class SuggestionRestaurant(
    val id: Int,
    val name: String
)

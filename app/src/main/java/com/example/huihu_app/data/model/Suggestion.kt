package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@Serializable
data class Suggestion(
    val id: Int,
    val content: String,
    val images: List<String>? = null,
    val type: String,
    val status: String,
    val reviewer_id: Int? = null,
    val review_comment: String? = null,
    val user_id: Int,
    val created_at: String,
    val reviewed_at: String? = null,
    val food: SuggestionFood? = null,
    val restaurant: SuggestionRestaurant? = null
)

@JsonIgnoreUnknownKeys
@Serializable
data class SuggestionFood(
    val id: Int,
    val name: String,
    val image: String? = null,
    val description: String? = null
)

@JsonIgnoreUnknownKeys
@Serializable
data class SuggestionRestaurant(
    val id: Int,
    val name: String,
    val description: String? = null,
    val location: String? = null,
    val image: String? = null
)

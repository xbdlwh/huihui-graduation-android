package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@JsonIgnoreUnknownKeys
@Serializable
data class SimpleRestaurant(
    val id: Int,
    val name: String
)

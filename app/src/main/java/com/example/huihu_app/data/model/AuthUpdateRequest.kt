package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthUpdateRequest(
    val email: String? = null,
    val username: String? = null,
    val profile: String? = null
)

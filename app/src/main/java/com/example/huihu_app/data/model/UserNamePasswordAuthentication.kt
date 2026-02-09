package com.example.huihu_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserNamePasswordAuthentication(val userName: String,val password: String)
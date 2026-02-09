package com.example.huihu_app.state

import com.example.huihu_app.data.model.CurrentUser

sealed interface AuthState {
    object UnAuthenticated: AuthState
    data class Authenticated(val currentUser: CurrentUser): AuthState
    object Loading: AuthState
}
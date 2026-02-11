package com.example.huihu_app.state

sealed interface AuthState {
    object UnAuthenticated: AuthState
    data class Authenticated(val token: String, val isNewUser: Boolean): AuthState
    object Loading: AuthState
}

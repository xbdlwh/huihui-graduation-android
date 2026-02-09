package com.example.huihu_app

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Nav {
    @Serializable
    object Login: Nav, NavKey
    @Serializable
    object Home: Nav, NavKey
    @Serializable
    object Splash: Nav, NavKey
}
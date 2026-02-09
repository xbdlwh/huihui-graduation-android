package com.example.huihu_app.ui.screen

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.huihu_app.Nav
import com.example.huihu_app.state.AuthState
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.AppViewModel

@Composable
fun AppScreen(viewModel: AppViewModel = viewModel(factory = AppViewModelProvider.FACTORY)) {
    val authState by viewModel.authState.collectAsStateWithLifecycle(Nav.Splash)

    val backStack = rememberNavBackStack()

    LaunchedEffect(authState) {
        backStack.clear()
        when(authState) {
            is AuthState.Loading -> {
                backStack.add(Nav.Splash)
            }
            is AuthState.UnAuthenticated -> {
                backStack.add(Nav.Login)
            }
            is AuthState.Authenticated -> {
                backStack.add(Nav.Home)
            }
        }
    }

    if (!backStack.isEmpty()) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                entry<Nav.Login>() {
                    LoginScreen()
                }
                entry<Nav.Home>() {
                    HomeScreen()
                }
                entry<Nav.Splash>() {
                    SplashScreen()
                }
            }
        )
    }

}
package com.example.huihu_app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.LoginForm
import com.example.huihu_app.ui.viewModel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.FACTORY)) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold() {paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            LoginForm(uiState = uiState) {username,password ->
                viewModel.login(username,password)
            }
        }
    }
}



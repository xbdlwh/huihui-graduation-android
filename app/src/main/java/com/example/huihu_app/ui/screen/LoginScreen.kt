package com.example.huihu_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.components.LoginForm
import com.example.huihu_app.ui.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.FACTORY)
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorScheme.primaryContainer.copy(alpha = 0.22f),
                            colorScheme.secondaryContainer.copy(alpha = 0.16f),
                            colorScheme.background
                        )
                    )
                )
                .padding(paddingValues)
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 36.dp, end = 24.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.2f))
                    .fillMaxWidth(0.22f)
                    .height(96.dp)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 110.dp, start = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colorScheme.tertiary.copy(alpha = 0.15f))
                    .fillMaxWidth(0.3f)
                    .height(72.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "欢迎回来",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "登录后继续探索今天吃什么",
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                LoginForm(
                    modifier = Modifier.fillMaxWidth(),
                    uiState = uiState,
                    onLogin = { username, password ->
                        viewModel.login(username, password)
                    },
                    onRegister = onRegister
                )
            }
        }
    }
}

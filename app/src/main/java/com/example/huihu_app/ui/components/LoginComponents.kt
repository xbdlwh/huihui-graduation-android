package com.example.huihu_app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.huihu_app.ui.viewModel.AuthUiState

@Composable
fun LoginForm(modifier: Modifier = Modifier,uiState: AuthUiState,onLogin:(String,String) -> Unit) {

    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Card(modifier) {
        Column {
            OutlinedTextField(
                value = userName,
                onValueChange = {userName = it}
            )
            OutlinedTextField(
                value = password,
                onValueChange = {password = it}
            )
            Button(onClick = {onLogin(userName,password)}, enabled = !uiState.isLoading) {
                Text("Login")
            }
            if (uiState.error != null) {
                Text(uiState.error)
            }
        }
    }
}
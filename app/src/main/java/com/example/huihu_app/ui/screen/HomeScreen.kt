package com.example.huihu_app.ui.screen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huihu_app.ui.AppViewModelProvider
import com.example.huihu_app.ui.viewModel.HomeViewModel


@Composable
fun HomeScreen(token: String, viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.FACTORY)) {
    Button(onClick = {
        viewModel.logout()
    }) {
        Text("HOME $token")
    }
}

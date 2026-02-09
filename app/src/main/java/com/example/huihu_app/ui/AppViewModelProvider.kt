package com.example.huihu_app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.huihu_app.MainApp
import com.example.huihu_app.ui.viewModel.AppViewModel
import com.example.huihu_app.ui.viewModel.AuthViewModel

class AppViewModelProvider {
    companion object {
        val FACTORY = viewModelFactory {
            initializer {
                AppViewModel(
                    container().localStoreRepository
                )
            }
            initializer {
                AuthViewModel(
                    container().authRepository,
                    container().localStoreRepository
                )
            }
        }
    }
}
fun CreationExtras.container() = (this[APPLICATION_KEY] as MainApp).container
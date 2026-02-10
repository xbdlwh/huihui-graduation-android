package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.repository.LocalStoreRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    val localStoreRepository: LocalStoreRepository
): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            localStoreRepository.logout()
        }
    }
}
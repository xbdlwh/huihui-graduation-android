package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.FoodRepository
import com.example.huihu_app.data.repository.LocalStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedTab: Int = 1,
    val isRandomMode: Boolean = false,
    val currentUserProfileUrl: String? = null,
    val isLoadingUser: Boolean = false
)

class HomeViewModel(
    private val localStoreRepository: LocalStoreRepository,
    private val foodRepository: FoodRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private var profileLoadedForToken: String? = null

    init {
        viewModelScope.launch {
            localStoreRepository.isRandomMode.collectLatest { enabled ->
                _uiState.update {
                    it.copy(isRandomMode = enabled)
                }
            }
        }
    }

    fun selectTab(tab: Int) {
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }

    fun logout() {
        viewModelScope.launch {
            foodRepository.clearCache()
            localStoreRepository.logout()
        }
    }

    fun setRandomMode(enabled: Boolean) {
        viewModelScope.launch {
            localStoreRepository.setRandomMode(enabled)
        }
    }

    fun loadUserProfile(token: String) {
        if (_uiState.value.isLoadingUser) return
        if (profileLoadedForToken == token && _uiState.value.currentUserProfileUrl != null) return

        _uiState.update { it.copy(isLoadingUser = true) }
        viewModelScope.launch {
            val res = authRepository.me(token)
            if (res.isSuccess()) {
                profileLoadedForToken = token
                _uiState.update {
                    it.copy(
                        currentUserProfileUrl = res.data?.profile,
                        isLoadingUser = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoadingUser = false) }
            }
        }
    }
}

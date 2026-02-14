package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.CurrentUser
import com.example.huihu_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MineUiState(
    val user: CurrentUser? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MineViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MineUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMe(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = authRepository.me(token)
            if (!response.isSuccess()) {
                _uiState.update { it.copy(isLoading = false, error = response.message) }
                return@launch
            }
            _uiState.update {
                it.copy(
                    user = response.data,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
}

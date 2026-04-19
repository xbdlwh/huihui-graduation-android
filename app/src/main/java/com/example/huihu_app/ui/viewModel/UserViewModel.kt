package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.UserProfile
import com.example.huihu_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProfile(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = userRepository.getProfile(token)
            if (response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        profile = response.data,
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = response.message
                    )
                }
            }
        }
    }

    fun updateProfile(
        token: String,
        heightCm: Double? = null,
        weightKg: Double? = null,
        birthDate: String? = null,
        gender: String? = null
    ) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val response = userRepository.updateProfile(
                token = token,
                heightCm = heightCm,
                weightKg = weightKg,
                birthDate = birthDate,
                gender = gender
            )
            if (response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        profile = response.data,
                        isSaving = false,
                        saved = true,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = response.message
                    )
                }
            }
        }
    }

    fun clearSavedFlag() {
        _uiState.update { it.copy(saved = false) }
    }
}
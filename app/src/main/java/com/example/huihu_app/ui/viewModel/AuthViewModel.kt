package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.LocalStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    val authRepository: AuthRepository,
    val localStoreRepository: LocalStoreRepository
): ViewModel() {

    private var _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()


    fun login(userName: String, password: String) {
        _uiState.update {
            it.copy(isLoading =  true, error = null)
        }
        viewModelScope.launch {
            val res = authRepository.login(userName,password)
            if (res.isSuccess()) {
                localStoreRepository.saveToken(res.data!!.token, isNewUser = false)
            }else {
                _uiState.update {
                    it.copy(error =  res.message)
                }
            }
            _uiState.update {
                it.copy(isLoading =  false)
            }
        }
    }

    fun register(email: String, userName: String, password: String) {
        _uiState.update {
            it.copy(isLoading = true, error = null)
        }
        viewModelScope.launch {
            val res = authRepository.register(email, userName, password)
            if (res.isSuccess()) {
                localStoreRepository.saveToken(res.data!!.token, isNewUser = true)
            } else {
                _uiState.update {
                    it.copy(error = res.message)
                }
            }
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}

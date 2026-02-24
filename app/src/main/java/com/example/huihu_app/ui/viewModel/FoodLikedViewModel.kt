package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.LikedFood
import com.example.huihu_app.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodLikedUiState(
    val isLoading: Boolean = false,
    val foods: List<LikedFood> = emptyList(),
    val error: String? = null
)

class FoodLikedViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodLikedUiState())
    val uiState = _uiState.asStateFlow()

    fun loadLikedFoods(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = foodRepository.likedFoods(token)
            if (!response.isSuccess()) {
                _uiState.update { it.copy(isLoading = false, error = response.message) }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    foods = response.data.orEmpty(),
                    error = null
                )
            }
        }
    }
}

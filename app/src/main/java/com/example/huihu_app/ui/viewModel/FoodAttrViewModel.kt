package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.FoodAttribute
import com.example.huihu_app.data.repository.FoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodAttrUiState(
    val isLoading: Boolean = false,
    val foodAttribute: FoodAttribute? = null,
    val error: String? = null
)

class FoodAttrViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FoodAttrUiState())
    val uiState = _uiState.asStateFlow()

    fun loadFoodAttribute(token: String, foodId: Int) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = foodRepository.foodAttribute(token, foodId)
            if (!response.isSuccess()) {
                _uiState.update { it.copy(isLoading = false, error = response.message) }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    foodAttribute = response.data,
                    error = null
                )
            }
        }
    }
}

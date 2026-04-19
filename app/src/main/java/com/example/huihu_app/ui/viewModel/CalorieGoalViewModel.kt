package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.CalorieGoal
import com.example.huihu_app.data.repository.CalorieGoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalorieGoalUiState(
    val calorieGoal: CalorieGoal? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class CalorieGoalViewModel(
    private val calorieGoalRepository: CalorieGoalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalorieGoalUiState())
    val uiState = _uiState.asStateFlow()

    fun loadCalorieGoal(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = calorieGoalRepository.getCalorieGoal(token)
            if (response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        calorieGoal = response.data,
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

    fun createCalorieGoal(
        token: String,
        dailyCalorieGoal: Double,
        effectiveFrom: String
    ) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val response = calorieGoalRepository.createCalorieGoal(
                token = token,
                dailyCalorieGoal = dailyCalorieGoal,
                effectiveFrom = effectiveFrom
            )
            if (response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        calorieGoal = response.data,
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
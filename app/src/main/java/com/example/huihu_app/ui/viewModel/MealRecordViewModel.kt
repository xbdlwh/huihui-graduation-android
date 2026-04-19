package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.MealRecord
import com.example.huihu_app.data.repository.MealRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MealRecordUiState(
    val mealRecords: List<MealRecord> = emptyList(),
    val totalCaloriesToday: Double = 0.0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

class MealRecordViewModel(
    private val mealRecordRepository: MealRecordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MealRecordUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMealRecords(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = mealRecordRepository.getMealRecords(token)
            if (response.isSuccess()) {
                val records = response.data.orEmpty()
                val totalCalories = records.sumOf { it.total_calories }
                _uiState.update {
                    it.copy(
                        mealRecords = records,
                        totalCaloriesToday = totalCalories,
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

    fun createMealRecord(token: String, foodId: Int, mealType: String) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val response = mealRecordRepository.createMealRecord(token, foodId, mealType)
            if (response.isSuccess()) {
                loadMealRecords(token)
                _uiState.update { it.copy(isSaving = false) }
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
}
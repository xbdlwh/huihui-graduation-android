package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.ExerciseRecord
import com.example.huihu_app.data.model.ExerciseType
import com.example.huihu_app.data.repository.ExerciseRecordRepository
import com.example.huihu_app.data.repository.ExerciseTypeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseRecordUiState(
    val exerciseTypes: List<ExerciseType> = emptyList(),
    val exerciseRecords: List<ExerciseRecord> = emptyList(),
    val totalCaloriesBurnedToday: Double = 0.0,
    val selectedExerciseTypeId: Int? = null,
    val durationMinutes: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class ExerciseRecordViewModel(
    private val exerciseTypeRepository: ExerciseTypeRepository,
    private val exerciseRecordRepository: ExerciseRecordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExerciseRecordUiState())
    val uiState = _uiState.asStateFlow()

    fun loadExerciseTypes() {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = exerciseTypeRepository.getExerciseTypes()
            if (response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        exerciseTypes = response.data.orEmpty(),
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

    fun loadExerciseRecords(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val response = exerciseRecordRepository.getExerciseRecords(token)
            if (response.isSuccess()) {
                val records = response.data.orEmpty()
                val totalCalories = records.sumOf { it.calories_burned }
                _uiState.update {
                    it.copy(
                        exerciseRecords = records,
                        totalCaloriesBurnedToday = totalCalories,
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

    fun selectExerciseType(exerciseTypeId: Int) {
        _uiState.update { it.copy(selectedExerciseTypeId = exerciseTypeId) }
    }

    fun updateDuration(minutes: Int) {
        _uiState.update { it.copy(durationMinutes = minutes) }
    }

    fun createExerciseRecord(token: String, bodyWeightKg: Double) {
        val state = _uiState.value
        val exerciseTypeId = state.selectedExerciseTypeId ?: return
        if (state.durationMinutes <= 0) return
        if (_uiState.value.isSaving) return

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val occurredAt = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00"
            val response = exerciseRecordRepository.createExerciseRecord(
                token = token,
                exerciseTypeId = exerciseTypeId,
                durationMinutes = state.durationMinutes,
                bodyWeightKg = bodyWeightKg,
                occurredAt = occurredAt
            )
            if (response.isSuccess()) {
                loadExerciseRecords(token)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saved = true,
                        selectedExerciseTypeId = null,
                        durationMinutes = 0,
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
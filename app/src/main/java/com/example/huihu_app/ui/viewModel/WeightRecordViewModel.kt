package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.ExerciseRecord
import com.example.huihu_app.data.model.ExerciseType
import com.example.huihu_app.data.repository.CalorieGoalRepository
import com.example.huihu_app.data.repository.ExerciseRecordRepository
import com.example.huihu_app.data.repository.ExerciseTypeRepository
import com.example.huihu_app.data.repository.MealRecordRepository
import com.example.huihu_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeightRecordUiState(
    val calorieGoal: Double? = null,
    val mealRecords: List<com.example.huihu_app.data.model.MealRecord> = emptyList(),
    val totalCaloriesConsumed: Double = 0.0,
    val exerciseTypes: List<ExerciseType> = emptyList(),
    val exerciseRecords: List<ExerciseRecord> = emptyList(),
    val totalCaloriesBurned: Double = 0.0,
    val userWeight: Double = 0.0,
    val selectedExerciseTypeId: Int? = null,
    val durationMinutes: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

class WeightRecordViewModel(
    private val calorieGoalRepository: CalorieGoalRepository,
    private val mealRecordRepository: MealRecordRepository,
    private val exerciseTypeRepository: ExerciseTypeRepository,
    private val exerciseRecordRepository: ExerciseRecordRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeightRecordUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(token: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Load user profile for weight
            val userRes = userRepository.getProfile(token)
            if (userRes.isSuccess()) {
                _uiState.update { it.copy(userWeight = userRes.data?.weight_kg ?: 0.0) }
            }
        }

        viewModelScope.launch {
            // Load calorie goal
            val goalRes = calorieGoalRepository.getCalorieGoal(token)
            if (goalRes.isSuccess()) {
                _uiState.update { it.copy(calorieGoal = goalRes.data?.daily_calorie_goal) }
            }
        }

        viewModelScope.launch {
            // Load meal records
            val mealRes = mealRecordRepository.getMealRecords(token)
            if (mealRes.isSuccess()) {
                val records = mealRes.data.orEmpty()
                val total = records.sumOf { it.total_calories }
                _uiState.update {
                    it.copy(
                        mealRecords = records,
                        totalCaloriesConsumed = total
                    )
                }
            }
        }

        viewModelScope.launch {
            // Load exercise types
            val typeRes = exerciseTypeRepository.getExerciseTypes()
            if (typeRes.isSuccess()) {
                _uiState.update { it.copy(exerciseTypes = typeRes.data.orEmpty()) }
            }
        }

        viewModelScope.launch {
            // Load exercise records
            val exRes = exerciseRecordRepository.getExerciseRecords(token)
            if (exRes.isSuccess()) {
                val records = exRes.data.orEmpty()
                val totalBurned = records.sumOf { it.calories_burned }
                _uiState.update {
                    it.copy(
                        exerciseRecords = records,
                        totalCaloriesBurned = totalBurned,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectExerciseType(exerciseTypeId: Int) {
        _uiState.update { it.copy(selectedExerciseTypeId = exerciseTypeId) }
    }

    fun updateDuration(minutes: Int) {
        _uiState.update { it.copy(durationMinutes = minutes) }
    }

    fun createExerciseRecord(token: String) {
        val state = _uiState.value
        val exerciseTypeId = state.selectedExerciseTypeId ?: return
        if (state.durationMinutes <= 0) return
        if (state.isSaving) return

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val occurredAt = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00"
            val response = exerciseRecordRepository.createExerciseRecord(
                token = token,
                exerciseTypeId = exerciseTypeId,
                durationMinutes = state.durationMinutes,
                bodyWeightKg = state.userWeight,
                occurredAt = occurredAt
            )
            if (response.isSuccess()) {
                loadData(token)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        selectedExerciseTypeId = null,
                        durationMinutes = 0
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

    fun updateCalorieGoal(token: String, goal: Double) {
        _uiState.update { it.copy(calorieGoal = goal) }
    }
}
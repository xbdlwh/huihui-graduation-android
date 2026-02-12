package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.Food
import com.example.huihu_app.data.model.FoodReactionRequest
import com.example.huihu_app.data.repository.FoodRepository
import com.example.huihu_app.data.repository.LocalStoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodRecommendationUiState(
    val currentFood: Food? = null,
    val cachedCount: Int = 0,
    val isLoading: Boolean = false,
    val isRefilling: Boolean = false,
    val error: String? = null,
    val acceptedFoodId: Int? = null,
    val feedbackMessage: String? = null
)

class FoodRecommendationViewModel(
    private val foodRepository: FoodRepository,
    private val localStoreRepository: LocalStoreRepository
) : ViewModel() {

    companion object {
        private const val LOW_CACHE_THRESHOLD = 3
        private const val CACHE_LIMIT = 100
    }

    private val _uiState = MutableStateFlow(FoodRecommendationUiState())
    val uiState = _uiState.asStateFlow()

    private var authToken: String? = null
    private var refillJob: Job? = null

    fun load(token: String, force: Boolean = false) {
        if (authToken == null) {
            authToken = token
        } else if (authToken != token) {
            authToken = token
        }

        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            refreshCurrentFood(forceRemote = force)
        }
    }

    fun retry() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            refreshCurrentFood(forceRemote = true)
        }
    }

    fun onThatsIt() {
        val current = _uiState.value.currentFood ?: return
        sendReactionAsync(
            FoodReactionRequest(
                food_id = current.id,
                reaction = "like",
                source = "food_tab",
                occurred_at = System.currentTimeMillis() / 1000
            )
        )
        viewModelScope.launch {
            localStoreRepository.saveTodayFoodId(current.id)
        }
        _uiState.update {
            it.copy(
                acceptedFoodId = current.id,
                feedbackMessage = "Great choice. Enjoy your meal."
            )
        }
    }

    fun onChangeIt() {
        val current = _uiState.value.currentFood ?: return
        sendReactionAsync(
            FoodReactionRequest(
                food_id = current.id,
                reaction = "skip",
                source = "food_tab",
                occurred_at = System.currentTimeMillis() / 1000
            )
        )
        viewModelScope.launch {
            removeCurrentAndLoadNext(current.id, null)
        }
    }

    fun onDontLikeIt() {
        val current = _uiState.value.currentFood ?: return
        sendReactionAsync(
            FoodReactionRequest(
                food_id = current.id,
                reaction = "dislike",
                source = "food_tab",
                occurred_at = System.currentTimeMillis() / 1000
            )
        )
        viewModelScope.launch {
            removeCurrentAndLoadNext(current.id, "Noted. We will refine your recommendations.")
        }
    }

    fun nextFood() {
        val current = _uiState.value.currentFood ?: return
        viewModelScope.launch {
            removeCurrentAndLoadNext(current.id, null)
        }
    }

    private suspend fun removeCurrentAndLoadNext(foodId: Int, message: String?) {
        val today = localStoreRepository.getTodayFoodIdOrNull()
        foodRepository.removeCachedFood(foodId)
        if (today == foodId) {
            localStoreRepository.clearTodayFoodId()
        }
        _uiState.update {
            it.copy(acceptedFoodId = null, feedbackMessage = message)
        }
        refreshCurrentFood(forceRemote = false)
    }

    private suspend fun refreshCurrentFood(forceRemote: Boolean) {
        val token = authToken ?: return
        _uiState.update {
            it.copy(isLoading = true, error = null)
        }

        if (forceRemote || foodRepository.getTopCachedFood() == null) {
            val fetched = fetchAndCache(token)
            if (!fetched && foodRepository.getTopCachedFood() == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load recommendations. Please retry."
                    )
                }
                return
            }
        }

        val topFood = foodRepository.getTopCachedFood()
        val count = foodRepository.getCachedCount()
        val todayFoodId = localStoreRepository.getTodayFoodIdOrNull()

        _uiState.update {
            it.copy(
                currentFood = topFood,
                cachedCount = count,
                isLoading = false,
                acceptedFoodId = if (todayFoodId == topFood?.id) todayFoodId else null,
                feedbackMessage = if (todayFoodId == topFood?.id) "Great choice. Enjoy your meal." else it.feedbackMessage
            )
        }

        if (count <= LOW_CACHE_THRESHOLD) {
            triggerBackgroundRefill(token)
        }
    }

    private fun triggerBackgroundRefill(token: String) {
        if (refillJob?.isActive == true) return

        refillJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isRefilling = true)
            }
            val fetched = fetchAndCache(token)
            if (fetched) {
                val count = foodRepository.getCachedCount()
                _uiState.update {
                    it.copy(cachedCount = count)
                }
            }
            _uiState.update {
                it.copy(isRefilling = false)
            }
        }
    }

    private suspend fun fetchAndCache(token: String): Boolean {
        val res = foodRepository.recommendation(token)
        if (!res.isSuccess()) {
            return false
        }
        val foods = res.data ?: emptyList()
        foodRepository.saveRecommendationsToCache(foods)
        foodRepository.trimCache(CACHE_LIMIT)
        return true
    }

    private fun sendReactionAsync(request: FoodReactionRequest) {
        viewModelScope.launch {
            val token = authToken ?: return@launch
            foodRepository.reaction(
                token = token,
                foodId = request.food_id,
                reaction = request.reaction,
                source = request.source,
                occurredAt = request.occurred_at
            )
        }
    }
}

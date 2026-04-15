package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.Food
import com.example.huihu_app.data.model.FoodComment
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
    val feedbackMessage: String? = null,
    val comments: List<FoodComment> = emptyList(),
    val isLoadingComments: Boolean = false
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
    private var isRandomMode: Boolean = false

    fun onRandomModeChanged(enabled: Boolean) {
        if (isRandomMode == enabled) return
        isRandomMode = enabled
        authToken ?: return
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            refreshCurrentFood(forceRemote = false)
        }
    }

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
                feedbackMessage = "不错的选择，祝你用餐愉快。"
            )
        }
        loadComments(current.id)
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
            removeCurrentAndLoadNext(current.id, "已记录，我们会优化你的推荐。")
        }
    }

    fun nextFood() {
        val current = _uiState.value.currentFood ?: return
        viewModelScope.launch {
            removeCurrentAndLoadNext(current.id, null)
        }
    }

    private fun loadComments(foodId: Int) {
        val token = authToken ?: return
        _uiState.update { it.copy(isLoadingComments = true) }
        viewModelScope.launch {
            val response = foodRepository.foodComments(token = token, foodId = foodId)
            _uiState.update {
                it.copy(
                    comments = response.data.orEmpty(),
                    isLoadingComments = false
                )
            }
        }
    }

    private suspend fun removeCurrentAndLoadNext(foodId: Int, message: String?) {
        val today = localStoreRepository.getTodayFoodIdOrNull()
        foodRepository.removeCachedFood(foodId = foodId, isRandom = isRandomMode)
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

        if (forceRemote || foodRepository.getTopCachedFood(isRandom = isRandomMode) == null) {
            val fetched = fetchAndCache(token)
            if (!fetched && foodRepository.getTopCachedFood(isRandom = isRandomMode) == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "加载推荐失败，请重试。"
                    )
                }
                return
            }
        }

        val topFood = foodRepository.getTopCachedFood(isRandom = isRandomMode)
        val count = foodRepository.getCachedCount(isRandom = isRandomMode)
        val todayFoodId = localStoreRepository.getTodayFoodIdOrNull()

        _uiState.update {
            it.copy(
                currentFood = topFood,
                cachedCount = count,
                isLoading = false,
                acceptedFoodId = if (todayFoodId == topFood?.id) todayFoodId else null,
                feedbackMessage = if (todayFoodId == topFood?.id) "不错的选择，祝你用餐愉快。" else it.feedbackMessage
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
                val count = foodRepository.getCachedCount(isRandom = isRandomMode)
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
        val res = foodRepository.recommendation(token = token, isRandom = isRandomMode)
        if (!res.isSuccess()) {
            return false
        }
        val foods = res.data ?: emptyList()
        foodRepository.saveRecommendationsToCache(foods = foods, isRandom = isRandomMode)
        foodRepository.trimCache(limit = CACHE_LIMIT, isRandom = isRandomMode)
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

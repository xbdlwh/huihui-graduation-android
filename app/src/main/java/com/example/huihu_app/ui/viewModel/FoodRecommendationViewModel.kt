package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.Food
import com.example.huihu_app.data.model.FoodReactionRequest
import com.example.huihu_app.data.repository.FoodRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FoodRecommendationUiState(
    val cards: List<Food> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val pendingReactionCount: Int = 0,
    val acceptedFoodId: Int? = null,
    val feedbackMessage: String? = null
)

class FoodRecommendationViewModel(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodRecommendationUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        private const val MAX_REACTION_RETRY = 3
    }

    private var authToken: String? = null
    private var hasLoaded = false

    fun load(token: String, force: Boolean = false) {
        if (authToken == null) {
            authToken = token
        } else if (authToken != token) {
            authToken = token
            hasLoaded = false
        }

        if (_uiState.value.isLoading) return

        val shouldFetch = force || !hasLoaded || _uiState.value.cards.isEmpty()
        if (!shouldFetch) return

        fetchRecommendation()
    }

    fun retry() {
        if (_uiState.value.isLoading) return
        fetchRecommendation()
    }

    fun onThatsIt() {
        val topCard = _uiState.value.cards.firstOrNull() ?: return
        sendReactionAsync(
            FoodReactionRequest(
                food_id = topCard.id,
                reaction = "like",
                source = "food_tab",
                occurred_at = System.currentTimeMillis() / 1000
            )
        )
        _uiState.update {
            it.copy(
                acceptedFoodId = topCard.id,
                feedbackMessage = "Great choice. Enjoy your meal."
            )
        }
    }

    fun onChangeIt() {
        val topCard = _uiState.value.cards.firstOrNull() ?: return
        sendReactionAsync(
            FoodReactionRequest(
                food_id = topCard.id,
                reaction = "skip",
                source = "food_tab",
                occurred_at = System.currentTimeMillis() / 1000
            )
        )
        _uiState.update {
            it.copy(acceptedFoodId = null, feedbackMessage = null)
        }
        fetchRecommendation()
    }

    fun onDontLikeIt() {
        val topCard = _uiState.value.cards.firstOrNull() ?: return
        sendReactionAsync(
            FoodReactionRequest(
                food_id = topCard.id,
                reaction = "dislike",
                source = "food_tab",
                occurred_at = System.currentTimeMillis() / 1000
            )
        )
        _uiState.update {
            it.copy(
                acceptedFoodId = null,
                feedbackMessage = "Noted. We will refine your recommendations."
            )
        }
    }

    private fun fetchRecommendation() {
        if (_uiState.value.isLoading) return
        val token = authToken ?: return
        _uiState.update {
            it.copy(isLoading = true, error = null, acceptedFoodId = null, feedbackMessage = null)
        }

        viewModelScope.launch {
            val res = foodRepository.recommendation(token)
            if (res.isSuccess()) {
                hasLoaded = true
                _uiState.update {
                    it.copy(
                        cards = res.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = res.message)
                }
            }
        }
    }

    private fun sendReactionAsync(request: FoodReactionRequest) {
        _uiState.update {
            it.copy(pendingReactionCount = it.pendingReactionCount + 1)
        }

        viewModelScope.launch {
            val token = authToken
            if (token == null) {
                _uiState.update {
                    it.copy(pendingReactionCount = (it.pendingReactionCount - 1).coerceAtLeast(0))
                }
                return@launch
            }

            val response = foodRepository.reaction(
                token = token,
                foodId = request.food_id,
                reaction = request.reaction,
                source = request.source,
                occurredAt = request.occurred_at
            )
            if (response.isSuccess()) {
                _uiState.update {
                    it.copy(pendingReactionCount = (it.pendingReactionCount - 1).coerceAtLeast(0))
                }
                return@launch
            }
        }
    }
}

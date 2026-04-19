package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.CurrentUser
import com.example.huihu_app.data.model.UserProfile
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.FoodRepository
import com.example.huihu_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MineUiState(
    val user: CurrentUser? = null,
    val userProfile: UserProfile? = null,
    val likeCount: Int = 0,
    val dislikeCount: Int = 0,
    val topTagNames: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MineViewModel(
    private val authRepository: AuthRepository,
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MineUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMe(token: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val meResponse = authRepository.me(token)
            val countResponse = foodRepository.reactionCount(token)
            val topTagsResponse = foodRepository.topTags(token)
            val profileResponse = userRepository.getProfile(token)

            if (!meResponse.isSuccess()) {
                _uiState.update { it.copy(isLoading = false, error = meResponse.message) }
                return@launch
            }

            val reactionCount = if (countResponse.isSuccess()) {
                countResponse.data
            } else {
                null
            }
            val topTagNames = if (topTagsResponse.isSuccess()) {
                topTagsResponse.data.orEmpty()
                    .take(3)
                    .map { it.name.trim() }
                    .filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            _uiState.update {
                it.copy(
                    user = meResponse.data,
                    userProfile = profileResponse.data,
                    likeCount = reactionCount?.like ?: 0,
                    dislikeCount = reactionCount?.dislike ?: 0,
                    topTagNames = topTagNames,
                    isLoading = false,
                    error = null
                )
            }
        }
    }
}

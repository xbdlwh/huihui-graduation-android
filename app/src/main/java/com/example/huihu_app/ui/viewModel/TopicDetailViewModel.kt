package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.data.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TopicDetailUiState(
    val comments: List<Topic> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val likeOverrides: Map<Int, TopicLikeUi> = emptyMap(),
    val inFlightTopicIds: Set<Int> = emptySet(),
    val message: String? = null
)

class TopicDetailViewModel(
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadComments(token: String, topicId: Int) {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val response = topicRepository.comments(token, topicId)
            if (!response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = response.message
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    comments = response.data.orEmpty(),
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun onToggleLike(token: String, topic: Topic) {
        val state = _uiState.value
        if (topic.id in state.inFlightTopicIds) return

        val existing = state.likeOverrides[topic.id]
        val effectiveLiked = existing?.liked ?: topic.liked
        val effectiveCount = existing?.likeCount ?: topic.like_count
        val targetLike = !effectiveLiked
        val targetCount = if (targetLike) effectiveCount + 1 else maxOf(0, effectiveCount - 1)

        _uiState.update {
            it.copy(
                likeOverrides = it.likeOverrides + (topic.id to TopicLikeUi(targetLike, targetCount)),
                inFlightTopicIds = it.inFlightTopicIds + topic.id
            )
        }

        viewModelScope.launch {
            val response = topicRepository.setTopicLike(
                token = token,
                topicId = topic.id,
                like = targetLike
            )
            if (!response.isSuccess()) {
                _uiState.update {
                    it.copy(
                        likeOverrides = it.likeOverrides + (topic.id to TopicLikeUi(effectiveLiked, effectiveCount)),
                        message = response.message
                    )
                }
            }
            _uiState.update {
                it.copy(inFlightTopicIds = it.inFlightTopicIds - topic.id)
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

package com.example.huihu_app.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.data.repository.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TopicLikeUi(
    val liked: Boolean,
    val likeCount: Int
)

data class ForumUiState(
    val likeOverrides: Map<Int, TopicLikeUi> = emptyMap(),
    val inFlightTopicIds: Set<Int> = emptySet(),
    val message: String? = null
)

class ForumViewModel(
    private val topicRepository: TopicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState = _uiState.asStateFlow()

    private var cachedToken: String? = null
    private var cachedFlow: Flow<PagingData<Topic>>? = null

    fun topics(token: String): Flow<PagingData<Topic>> {
        if (cachedToken != token || cachedFlow == null) {
            cachedToken = token
            cachedFlow = topicRepository.pager(token).flow.cachedIn(viewModelScope)
        }
        return cachedFlow!!
    }

    fun onToggleLike(token: String, topic: Topic) {
        val state = _uiState.value
        if (topic.id in state.inFlightTopicIds) {
            return
        }

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

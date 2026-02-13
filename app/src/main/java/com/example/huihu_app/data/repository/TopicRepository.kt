package com.example.huihu_app.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateTopicRequest
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.data.model.TopicLikeRequest
import com.example.huihu_app.data.source.TopicSource
import okhttp3.MultipartBody

private const val TAG = "TopicRepository"
class TopicRepository(
    private val topicSource: TopicSource,
) {
    fun pager(token: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 3,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { TopicPagingSource(topicSource, token) }
    )

    suspend fun uploadImages(parts: List<MultipartBody.Part>): ApiResponse<List<String>> =
        runCatching {
            topicSource.upload(parts)
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun createTopic(
        token: String,
        title: String,
        content: String,
        images: List<String>
    ): ApiResponse<Unit> =
        runCatching {
            Log.d(
                TAG, "createTopic: ${
                    CreateTopicRequest(
                        title = title,
                        content = content,
                        images = images
                    )
                }"
            )
            topicSource.createTopic(
                token = "Bearer $token",
                request = CreateTopicRequest(
                    title = title,
                    content = content,
                    images = images
                )
            )
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun setTopicLike(
        token: String,
        topicId: Int,
        like: Boolean
    ): ApiResponse<Unit?> = runCatching {
        topicSource.likeTopic(
            token = "Bearer $token",
            request = TopicLikeRequest(
                topic_id = topicId,
                like = like
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }
}

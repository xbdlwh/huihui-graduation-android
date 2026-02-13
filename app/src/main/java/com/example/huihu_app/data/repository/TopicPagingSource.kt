package com.example.huihu_app.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.huihu_app.data.model.Topic
import com.example.huihu_app.data.source.TopicSource

class TopicPagingSource(
    private val topicSource: TopicSource,
    private val token: String
) : PagingSource<Int, Topic>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Topic> {
        val page = params.key ?: 1
        return try {
            val response = topicSource.topics(
                token = "Bearer $token",
                page = page
            )
            if (!response.isSuccess()) {
                return LoadResult.Error(IllegalStateException(response.message))
            }
            val data = response.data ?: emptyList()
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Topic>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

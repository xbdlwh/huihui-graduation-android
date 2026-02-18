package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateSuggestionRequest
import com.example.huihu_app.data.model.Suggestion
import com.example.huihu_app.data.source.SuggestionSource

class SuggestionRepository(
    private val suggestionSource: SuggestionSource
) {
    suspend fun mySuggestions(token: String): ApiResponse<List<Suggestion>> =
        runCatching {
            suggestionSource.mySuggestions("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun createSuggestion(
        token: String,
        content: String,
        images: List<String>,
        type: String,
        foodId: Int? = null,
        restaurantId: Int? = null
    ): ApiResponse<Int?> = runCatching {
        suggestionSource.createSuggestion(
            token = "Bearer $token",
            request = CreateSuggestionRequest(
                content = content,
                images = images,
                type = type,
                food_id = foodId,
                restaurant_id = restaurantId
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }
}

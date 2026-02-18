package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateSuggestionRequest
import com.example.huihu_app.data.model.Suggestion
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface SuggestionSource {
    @GET("/suggestion/my")
    suspend fun mySuggestions(
        @Header("Authorization") token: String
    ): ApiResponse<List<Suggestion>>

    @POST("/suggestion")
    suspend fun createSuggestion(
        @Header("Authorization") token: String,
        @Body request: CreateSuggestionRequest
    ): ApiResponse<Int?>
}

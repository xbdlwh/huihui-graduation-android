package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.ConsecutiveSuggestRequest
import com.example.huihu_app.data.model.Food
import com.example.huihu_app.data.model.FoodReactionCount
import com.example.huihu_app.data.model.FoodReactionRequest
import com.example.huihu_app.data.model.FoodTag
import com.example.huihu_app.data.model.LikedFood
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FoodSource {
    @POST("/food/consecutiveSuggest")
    suspend fun consecutiveSuggest(
        @Header("Authorization") token: String,
        @Body request: ConsecutiveSuggestRequest
    ): ApiResponse<List<Food>>

    @GET("/food/recommendation")
    suspend fun recommendation(
        @Header("Authorization") token: String
    ): ApiResponse<List<Food>>

    @POST("/food/recommendation/reaction")
    suspend fun reaction(
        @Header("Authorization") token: String,
        @Body request: FoodReactionRequest
    ): ApiResponse<Unit?>

    @GET("/food/recommendation/reaction/count")
    suspend fun reactionCount(
        @Header("Authorization") token: String
    ): ApiResponse<FoodReactionCount>

    @GET("/food/topTags")
    suspend fun topTags(
        @Header("Authorization") token: String
    ): ApiResponse<List<FoodTag>>

    @GET("/food/liked")
    suspend fun likedFoods(
        @Header("Authorization") token: String
    ): ApiResponse<List<LikedFood>>
}

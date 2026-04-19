package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.MealRecord
import com.example.huihu_app.data.model.CreateMealRecordRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MealRecordSource {
    @GET("/meal-record/")
    suspend fun getMealRecords(@Header("Authorization") token: String): ApiResponse<List<MealRecord>>

    @POST("/meal-record/inner")
    suspend fun createMealRecord(
        @Header("Authorization") token: String,
        @Body request: CreateMealRecordRequest
    ): ApiResponse<MealRecord>
}
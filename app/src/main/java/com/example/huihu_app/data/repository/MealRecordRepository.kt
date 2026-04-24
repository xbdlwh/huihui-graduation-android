package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateMealRecordRequest
import com.example.huihu_app.data.model.CreateOuterMealRecordRequest
import com.example.huihu_app.data.model.MealRecord
import com.example.huihu_app.data.source.MealRecordSource

class MealRecordRepository(
    val mealRecordSource: MealRecordSource
) {
    suspend fun getMealRecords(token: String): ApiResponse<List<MealRecord>> =
        runCatching {
            mealRecordSource.getMealRecords("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun createMealRecord(
        token: String,
        foodId: Int,
        mealType: String
    ): ApiResponse<MealRecord> = runCatching {
        mealRecordSource.createMealRecord(
            token = "Bearer $token",
            request = CreateMealRecordRequest(
                food_id = foodId,
                meal_type = mealType
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }

    suspend fun createOuterMealRecord(
        token: String,
        mealType: String,
        calories: Double
    ): ApiResponse<MealRecord> = runCatching {
        mealRecordSource.createOuterMealRecord(
            token = "Bearer $token",
            request = CreateOuterMealRecordRequest(
                meal_type = mealType,
                calories = calories
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }

    suspend fun recognizeFood(imageUrl: String): ApiResponse<Double> =
        runCatching {
            mealRecordSource.recognizeFood(imageUrl)
        }.getOrElse {
            return ApiResponse.from(it)
        }
}
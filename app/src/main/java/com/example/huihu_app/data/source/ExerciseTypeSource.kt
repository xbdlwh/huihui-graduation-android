package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.ExerciseType
import com.example.huihu_app.data.model.ExerciseTypeCreateRequest
import com.example.huihu_app.data.model.ExerciseTypeUpdateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseTypeSource {
    @GET("/exercise-type/")
    suspend fun getExerciseTypes(): ApiResponse<List<ExerciseType>>

    @GET("/exercise-type/{id}")
    suspend fun getExerciseType(@Path("id") id: Int): ApiResponse<ExerciseType>

    @POST("/exercise-type/")
    suspend fun createExerciseType(@Body request: ExerciseTypeCreateRequest): ApiResponse<ExerciseType>

    @POST("/exercise-type/update")
    suspend fun updateExerciseType(@Body request: ExerciseTypeUpdateRequest): ApiResponse<ExerciseType>

    @POST("/exercise-type/delete/{id}")
    suspend fun deleteExerciseType(@Path("id") id: Int): ApiResponse<Unit?>
}
package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateExerciseRecordRequest
import com.example.huihu_app.data.model.ExerciseRecord
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ExerciseRecordSource {
    @GET("/exercise-record/")
    suspend fun getExerciseRecords(@Header("Authorization") token: String): ApiResponse<List<ExerciseRecord>>

    @GET("/exercise-record/all")
    suspend fun getAllExerciseRecords(@Header("Authorization") token: String): ApiResponse<List<ExerciseRecord>>

    @POST("/exercise-record/")
    suspend fun createExerciseRecord(
        @Header("Authorization") token: String,
        @Body request: CreateExerciseRecordRequest
    ): ApiResponse<ExerciseRecord>
}
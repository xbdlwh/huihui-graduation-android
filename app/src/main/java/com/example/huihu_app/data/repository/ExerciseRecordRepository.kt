package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.CreateExerciseRecordRequest
import com.example.huihu_app.data.model.ExerciseRecord
import com.example.huihu_app.data.source.ExerciseRecordSource

class ExerciseRecordRepository(
    val exerciseRecordSource: ExerciseRecordSource
) {
    suspend fun getExerciseRecords(token: String): ApiResponse<List<ExerciseRecord>> =
        runCatching {
            exerciseRecordSource.getExerciseRecords("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun getAllExerciseRecords(token: String): ApiResponse<List<ExerciseRecord>> =
        runCatching {
            exerciseRecordSource.getAllExerciseRecords("Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun createExerciseRecord(
        token: String,
        exerciseTypeId: Int,
        durationMinutes: Int,
        bodyWeightKg: Double,
        occurredAt: String
    ): ApiResponse<ExerciseRecord> = runCatching {
        exerciseRecordSource.createExerciseRecord(
            token = "Bearer $token",
            request = CreateExerciseRecordRequest(
                exercise_type_id = exerciseTypeId,
                duration_minutes = durationMinutes,
                body_weight_kg = bodyWeightKg,
                occurred_at = occurredAt
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }
}
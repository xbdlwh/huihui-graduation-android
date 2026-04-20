package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.ExerciseType
import com.example.huihu_app.data.model.ExerciseTypeCreateRequest
import com.example.huihu_app.data.model.ExerciseTypeUpdateRequest
import com.example.huihu_app.data.source.ExerciseTypeSource

class ExerciseTypeRepository(
    val exerciseTypeSource: ExerciseTypeSource
) {
    suspend fun getExerciseTypes(): ApiResponse<List<ExerciseType>> =
        runCatching {
            exerciseTypeSource.getExerciseTypes()
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun getExerciseType(id: Int): ApiResponse<ExerciseType> =
        runCatching {
            exerciseTypeSource.getExerciseType(id)
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun createExerciseType(
        name: String,
        metValue: Double
    ): ApiResponse<ExerciseType> = runCatching {
        exerciseTypeSource.createExerciseType(
            request = ExerciseTypeCreateRequest(
                name = name,
                met_value = metValue
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }

    suspend fun updateExerciseType(
        id: Int,
        name: String,
        metValue: Double
    ): ApiResponse<ExerciseType> = runCatching {
        exerciseTypeSource.updateExerciseType(
            request = ExerciseTypeUpdateRequest(
                id = id,
                name = name,
                met_value = metValue
            )
        )
    }.getOrElse {
        return ApiResponse.from(it)
    }

    suspend fun deleteExerciseType(id: Int): ApiResponse<Unit?> =
        runCatching {
            exerciseTypeSource.deleteExerciseType(id)
        }.getOrElse {
            return ApiResponse.from(it)
        }
}
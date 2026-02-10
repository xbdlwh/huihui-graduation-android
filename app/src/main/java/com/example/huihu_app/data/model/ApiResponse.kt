package com.example.huihu_app.data.model

import android.util.Log
import kotlinx.serialization.Serializable

private const val TAG = "ApiResponse"
@Serializable
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> from(e: Throwable): ApiResponse<T> {
            Log.e(TAG, "from: ${e.toString()}")
            return ApiResponse(-1, e.message ?: "Unknown Error", null)
        }
    }
    fun isSuccess(): Boolean {
        return code == 200
    }
}
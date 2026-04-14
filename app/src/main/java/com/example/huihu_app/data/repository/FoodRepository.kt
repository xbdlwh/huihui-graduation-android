package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.ConsecutiveSuggestRequest
import com.example.huihu_app.data.model.Food
import com.example.huihu_app.data.model.FoodAttribute
import com.example.huihu_app.data.model.FoodReactionCount
import com.example.huihu_app.data.model.FoodReactionRequest
import com.example.huihu_app.data.model.FoodTag
import com.example.huihu_app.data.model.LikedFood
import com.example.huihu_app.data.local.dao.FoodCacheDao
import com.example.huihu_app.data.local.entity.toEntity
import com.example.huihu_app.data.local.entity.toFood
import com.example.huihu_app.data.source.FoodSource

class FoodRepository(
    private val foodSource: FoodSource,
    private val foodCacheDao: FoodCacheDao,
) {
    suspend fun consecutiveSuggest(
        token: String,
        foodIds: List<Int>,
        selectedFoodIds: List<Int>
    ): ApiResponse<List<Food>> =
        runCatching {
            foodSource.consecutiveSuggest(
                token = "Bearer $token",
                request = ConsecutiveSuggestRequest(
                    food_ids = foodIds,
                    selected_food_ids = selectedFoodIds
                )
            )
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun recommendation(token: String, isRandom: Boolean): ApiResponse<List<Food>> =
        runCatching {
            foodSource.recommendation(
                token = "Bearer $token",
                isRandom = if (isRandom) 1 else null
            )
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun reaction(
        token: String,
        foodId: Int,
        reaction: String,
        source: String,
        occurredAt: Long
    ): ApiResponse<Unit?> =
        runCatching {
            foodSource.reaction(
                token = "Bearer $token",
                request = FoodReactionRequest(
                    food_id = foodId,
                    reaction = reaction,
                    source = source,
                    occurred_at = occurredAt
                )
            )
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun reactionCount(token: String): ApiResponse<FoodReactionCount> =
        runCatching {
            foodSource.reactionCount(token = "Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun topTags(token: String): ApiResponse<List<FoodTag>> =
        runCatching {
            foodSource.topTags(token = "Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun likedFoods(token: String): ApiResponse<List<LikedFood>> =
        runCatching {
            foodSource.likedFoods(token = "Bearer $token")
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun foodAttribute(token: String, foodId: Int): ApiResponse<FoodAttribute> =
        runCatching {
            foodSource.foodAttribute(token = "Bearer $token", foodId = foodId)
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun saveRecommendationsToCache(foods: List<Food>, isRandom: Boolean) {
        if (foods.isEmpty()) return
        foodCacheDao.upsertAll(foods.map { it.toEntity(isRandom = isRandom) })
    }

    suspend fun getTopCachedFood(isRandom: Boolean): Food? =
        foodCacheDao.getTopOne(isRandom = isRandom)?.toFood()

    suspend fun getCachedCount(isRandom: Boolean): Int = foodCacheDao.count(isRandom = isRandom)

    suspend fun removeCachedFood(foodId: Int, isRandom: Boolean) {
        foodCacheDao.deleteById(id = foodId, isRandom = isRandom)
    }

    suspend fun trimCache(limit: Int = 100, isRandom: Boolean) {
        foodCacheDao.trimToLimit(limit = limit, isRandom = isRandom)
    }

    suspend fun clearCache() {
        foodCacheDao.clearAll()
    }
}

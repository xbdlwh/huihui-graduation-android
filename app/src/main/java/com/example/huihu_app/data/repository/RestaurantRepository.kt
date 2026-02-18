package com.example.huihu_app.data.repository

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.RestaurantFoodSimple
import com.example.huihu_app.data.model.SimpleRestaurant
import com.example.huihu_app.data.source.RestaurantSource

class RestaurantRepository(
    private val restaurantSource: RestaurantSource
) {
    suspend fun simpleRestaurants(): ApiResponse<List<SimpleRestaurant>> =
        runCatching {
            restaurantSource.simpleRestaurants()
        }.getOrElse {
            return ApiResponse.from(it)
        }

    suspend fun restaurantFoods(restaurantId: Int): ApiResponse<List<RestaurantFoodSimple>> =
        runCatching {
            restaurantSource.restaurantFoods(restaurantId)
        }.getOrElse {
            return ApiResponse.from(it)
        }
}

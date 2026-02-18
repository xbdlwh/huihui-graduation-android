package com.example.huihu_app.data.source

import com.example.huihu_app.data.model.ApiResponse
import com.example.huihu_app.data.model.RestaurantFoodSimple
import com.example.huihu_app.data.model.SimpleRestaurant
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantSource {
    @GET("/restaurant/simple")
    suspend fun simpleRestaurants(): ApiResponse<List<SimpleRestaurant>>

    @GET("/restaurant/foods")
    suspend fun restaurantFoods(
        @Query("restaurant_id") restaurantId: Int
    ): ApiResponse<List<RestaurantFoodSimple>>
}

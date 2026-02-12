package com.example.huihu_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.huihu_app.data.model.Food

@Entity(tableName = "food_cache")
data class FoodCacheEntity(
    @PrimaryKey
    val id: Int,
    val restaurantId: Int,
    val name: String,
    val description: String,
    val image: String,
    val createdAt: Long
)

fun Food.toEntity(now: Long = System.currentTimeMillis()): FoodCacheEntity =
    FoodCacheEntity(
        id = id,
        restaurantId = restaurant_id,
        name = name,
        description = description,
        image = image,
        createdAt = now
    )

fun FoodCacheEntity.toFood(): Food =
    Food(
        id = id,
        restaurant_id = restaurantId,
        name = name,
        description = description,
        image = image
    )

package com.example.huihu_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.huihu_app.data.local.entity.FoodCacheEntity

@Dao
interface FoodCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<FoodCacheEntity>)

    @Query("SELECT * FROM food_cache ORDER BY createdAt ASC")
    suspend fun getAllOrdered(): List<FoodCacheEntity>

    @Query("SELECT * FROM food_cache ORDER BY createdAt ASC LIMIT 1")
    suspend fun getTopOne(): FoodCacheEntity?

    @Query("SELECT COUNT(*) FROM food_cache")
    suspend fun count(): Int

    @Query("DELETE FROM food_cache WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM food_cache WHERE id NOT IN (SELECT id FROM food_cache ORDER BY createdAt DESC LIMIT :limit)")
    suspend fun trimToLimit(limit: Int)

    @Query("DELETE FROM food_cache")
    suspend fun clearAll()
}

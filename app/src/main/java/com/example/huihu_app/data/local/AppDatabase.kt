package com.example.huihu_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.huihu_app.data.local.dao.FoodCacheDao
import com.example.huihu_app.data.local.entity.FoodCacheEntity

@Database(
    entities = [FoodCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodCacheDao(): FoodCacheDao
}

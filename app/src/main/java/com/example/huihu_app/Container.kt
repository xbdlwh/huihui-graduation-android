package com.example.huihu_app

import android.content.Context
import androidx.room.Room
import com.example.huihu_app.data.local.AppDatabase
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.FoodRepository
import com.example.huihu_app.data.repository.LocalStoreRepository
import com.example.huihu_app.data.repository.TopicRepository
import com.example.huihu_app.data.source.AuthSource
import com.example.huihu_app.data.source.FoodSource
import com.example.huihu_app.data.source.TopicSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class AppContainer(context: Context) {

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val appDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "huihu_app.db"
        ).build()
    }

    val authSource by lazy {
        retrofit.create(AuthSource::class.java)
    }

    val foodSource by lazy {
        retrofit.create(FoodSource::class.java)
    }

    val topicSource by lazy {
        retrofit.create(TopicSource::class.java)
    }

    val foodCacheDao by lazy {
        appDatabase.foodCacheDao()
    }

    val localStoreRepository by lazy {
        LocalStoreRepository(context)
    }

    val authRepository by lazy {
        AuthRepository(authSource)
    }

    val foodRepository by lazy {
        FoodRepository(foodSource, foodCacheDao)
    }

    val topicRepository by lazy {
        TopicRepository(topicSource)
    }

    companion object {
        const val BASE_URL = "http://192.168.1.216:8899/"
    }
}

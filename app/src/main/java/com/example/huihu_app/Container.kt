package com.example.huihu_app

import android.content.Context
import androidx.room.Room
import com.example.huihu_app.data.local.AppDatabase
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.FoodRepository
import com.example.huihu_app.data.repository.LocalStoreRepository
import com.example.huihu_app.data.repository.RestaurantRepository
import com.example.huihu_app.data.repository.SuggestionRepository
import com.example.huihu_app.data.repository.TopicRepository
import com.example.huihu_app.data.source.AuthSource
import com.example.huihu_app.data.source.FoodSource
import com.example.huihu_app.data.source.RestaurantSource
import com.example.huihu_app.data.source.SuggestionSource
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
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .addMigrations(AppDatabase.MIGRATION_2_3)
            .build()
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

    val suggestionSource by lazy {
        retrofit.create(SuggestionSource::class.java)
    }

    val restaurantSource by lazy {
        retrofit.create(RestaurantSource::class.java)
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

    val suggestionRepository by lazy {
        SuggestionRepository(suggestionSource)
    }

    val restaurantRepository by lazy {
        RestaurantRepository(restaurantSource)
    }

    companion object {
        const val BASE_URL = "http://10.15.6.71:8899"
        const val BASE_URL_BACK_END = "http://10.15.6.71:3000"
    }
}

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
import com.example.huihu_app.data.repository.UserRepository
import com.example.huihu_app.data.repository.CalorieGoalRepository
import com.example.huihu_app.data.repository.MealRecordRepository
import com.example.huihu_app.data.repository.ExerciseTypeRepository
import com.example.huihu_app.data.repository.ExerciseRecordRepository
import com.example.huihu_app.data.repository.ImageRepository
import com.example.huihu_app.data.source.AuthSource
import com.example.huihu_app.data.source.FoodSource
import com.example.huihu_app.data.source.RestaurantSource
import com.example.huihu_app.data.source.SuggestionSource
import com.example.huihu_app.data.source.TopicSource
import com.example.huihu_app.data.source.UserSource
import com.example.huihu_app.data.source.CalorieGoalSource
import com.example.huihu_app.data.source.MealRecordSource
import com.example.huihu_app.data.source.ExerciseTypeSource
import com.example.huihu_app.data.source.ExerciseRecordSource
import com.example.huihu_app.data.source.ImageSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

class AppContainer(context: Context) {

    val okHttpClient = OkHttpClient.Builder()
        // 连接超时
        .connectTimeout(10, TimeUnit.SECONDS)
        // 读取超时
        .readTimeout(30, TimeUnit.SECONDS)
        // 写入超时
        .writeTimeout(30, TimeUnit.SECONDS)
        // 整个请求超时
        .callTimeout(60, TimeUnit.SECONDS)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
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
            .addMigrations(AppDatabase.MIGRATION_3_4)
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

    val userSource by lazy {
        retrofit.create(UserSource::class.java)
    }

    val calorieGoalSource by lazy {
        retrofit.create(CalorieGoalSource::class.java)
    }

    val mealRecordSource by lazy {
        retrofit.create(MealRecordSource::class.java)
    }

    val exerciseTypeSource by lazy {
        retrofit.create(ExerciseTypeSource::class.java)
    }

    val exerciseRecordSource by lazy {
        retrofit.create(ExerciseRecordSource::class.java)
    }

    val imageSource by lazy {
        retrofit.create(ImageSource::class.java)
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

    val userRepository by lazy {
        UserRepository(userSource)
    }

    val calorieGoalRepository by lazy {
        CalorieGoalRepository(calorieGoalSource)
    }

    val mealRecordRepository by lazy {
        MealRecordRepository(mealRecordSource)
    }

    val exerciseTypeRepository by lazy {
        ExerciseTypeRepository(exerciseTypeSource)
    }

    val exerciseRecordRepository by lazy {
        ExerciseRecordRepository(exerciseRecordSource)
    }

    val imageRepository by lazy {
        ImageRepository(imageSource)
    }

    companion object {
//        const val HOST = "mbp2.local"
        const val HOST = "192.168.1.6"
        const val BASE_URL = "http://$HOST:8899"
        const val BASE_URL_BACK_END = "http://$HOST:3000"
    }
}

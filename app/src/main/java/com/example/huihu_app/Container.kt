package com.example.huihu_app

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.huihu_app.data.repository.AuthRepository
import com.example.huihu_app.data.repository.LocalStoreRepository
import com.example.huihu_app.data.source.AuthSource
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


class AppContainer(context: Context) {

    private val retrofit = Retrofit.Builder().baseUrl("http://192.168.1.216:8899")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    val authSource by lazy {
        retrofit.create(AuthSource::class.java)
    }



    val localStoreRepository by lazy {
        LocalStoreRepository(context)
    }
    val authRepository by lazy {
        AuthRepository(authSource)
    }

}
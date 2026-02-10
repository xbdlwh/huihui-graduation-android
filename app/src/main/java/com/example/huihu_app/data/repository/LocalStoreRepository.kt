package com.example.huihu_app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.huihu_app.state.AuthState
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class LocalStoreRepository(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    val authState = dataStore.data.map {
         if (it[AUTH_TOKEN] == null) {
             AuthState.UnAuthenticated
        }else {
             AuthState.Authenticated(it[AUTH_TOKEN]!!)
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN] = token }
    }
    suspend fun logout() {
        dataStore.edit { it.remove(AUTH_TOKEN) }
    }
}

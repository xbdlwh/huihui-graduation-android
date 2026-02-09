package com.example.huihu_app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.huihu_app.data.model.CurrentUser
import com.example.huihu_app.state.AuthState
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class LocalStoreRepository(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val CURRENT_USER = stringPreferencesKey("current_user")
    }

    val currentUser = dataStore.data.map {
         if (it[CURRENT_USER] == null) {
             AuthState.UnAuthenticated
        }else {
             AuthState.Authenticated(Json.decodeFromString<CurrentUser>(it[CURRENT_USER]!!))
        }
    }

    suspend fun saveCurrentUser(currentUser: CurrentUser) {
        dataStore.edit { it[CURRENT_USER] = Json.encodeToString(currentUser) }
    }
    suspend fun logout() {
        dataStore.edit { it.remove(CURRENT_USER) }
    }
}

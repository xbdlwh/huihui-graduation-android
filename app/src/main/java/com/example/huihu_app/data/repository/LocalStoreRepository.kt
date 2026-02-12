package com.example.huihu_app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.huihu_app.state.AuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class LocalStoreRepository(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val IS_NEW_USER = booleanPreferencesKey("is_new_user")
        val OPEN_FOOD_TAB_ONCE = booleanPreferencesKey("open_food_tab_once")
        val TODAY_FOOD_ID = intPreferencesKey("today_food_id")
    }

    val authState = dataStore.data.map {
         if (it[AUTH_TOKEN] == null) {
             AuthState.UnAuthenticated
        }else {
             AuthState.Authenticated(
                 token = it[AUTH_TOKEN]!!,
                 isNewUser = it[IS_NEW_USER] ?: false
             )
        }
    }

    suspend fun saveToken(token: String, isNewUser: Boolean) {
        dataStore.edit {
            it[AUTH_TOKEN] = token
            it[IS_NEW_USER] = isNewUser
            it[OPEN_FOOD_TAB_ONCE] = false
        }
    }
    suspend fun logout() {
        dataStore.edit {
            it.remove(AUTH_TOKEN)
            it.remove(IS_NEW_USER)
            it.remove(OPEN_FOOD_TAB_ONCE)
            it.remove(TODAY_FOOD_ID)
        }
    }

    suspend fun markOnboardingCompleted() {
        dataStore.edit {
            it[IS_NEW_USER] = false
            it[OPEN_FOOD_TAB_ONCE] = true
        }
    }

    suspend fun consumeOpenFoodTabOnce(): Boolean {
        val shouldOpen = dataStore.data.first()[OPEN_FOOD_TAB_ONCE] ?: false
        if (shouldOpen) {
            dataStore.edit {
                it[OPEN_FOOD_TAB_ONCE] = false
            }
        }
        return shouldOpen
    }

    suspend fun saveTodayFoodId(foodId: Int) {
        dataStore.edit {
            it[TODAY_FOOD_ID] = foodId
        }
    }

    suspend fun getTodayFoodIdOrNull(): Int? = dataStore.data.first()[TODAY_FOOD_ID]

    suspend fun clearTodayFoodId() {
        dataStore.edit {
            it.remove(TODAY_FOOD_ID)
        }
    }
}

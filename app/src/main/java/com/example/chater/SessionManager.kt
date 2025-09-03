package com.example.chater

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    suspend fun saveUserSession(token: String, userId: String, userName: String){
        Log.d("Session Manager","Saving session Token: $token, UserID: $userId, Username: $userName")
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
        }
    }

    val userTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences->
            preferences[USER_TOKEN_KEY]
        }

    suspend fun clearSession(){
        Log.d("Session Manager","Clearing session")
        context.dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }

}
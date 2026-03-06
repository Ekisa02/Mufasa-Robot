package com.joseph.mufasarobot.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore


private val Context.dataStore by preferencesDataStore("auth")

interface TokenManager {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}

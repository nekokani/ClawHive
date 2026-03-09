package com.example.hybridagent.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        private val ANTHROPIC_API_KEY = stringPreferencesKey("anthropic_api_key")
        private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val EXECUTOR_PREFERENCE = stringPreferencesKey("executor_preference")
        private val COST_BUDGET = doublePreferencesKey("cost_budget")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val anthropicApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ANTHROPIC_API_KEY] ?: ""
    }

    val openaiApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[OPENAI_API_KEY] ?: ""
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: "http://localhost:3000"
    }

    val executorPreference: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EXECUTOR_PREFERENCE] ?: "AUTO"
    }

    val costBudget: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[COST_BUDGET] ?: 1.0
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "SYSTEM"
    }

    suspend fun saveAnthropicApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[ANTHROPIC_API_KEY] = key
        }
    }

    suspend fun saveOpenaiApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[OPENAI_API_KEY] = key
        }
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_URL] = url
        }
    }

    suspend fun saveExecutorPreference(preference: String) {
        context.dataStore.edit { preferences ->
            preferences[EXECUTOR_PREFERENCE] = preference
        }
    }

    suspend fun saveCostBudget(budget: Double) {
        context.dataStore.edit { preferences ->
            preferences[COST_BUDGET] = budget
        }
    }

    suspend fun saveThemeMode(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

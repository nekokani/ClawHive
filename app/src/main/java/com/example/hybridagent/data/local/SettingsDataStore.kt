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
        private val ANTHROPIC_BASE_URL = stringPreferencesKey("anthropic_base_url")
        private val ANTHROPIC_MODEL = stringPreferencesKey("anthropic_model")
        private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        private val OPENAI_BASE_URL = stringPreferencesKey("openai_base_url")
        private val OPENAI_MODEL = stringPreferencesKey("openai_model")
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val EXECUTOR_PREFERENCE = stringPreferencesKey("executor_preference")
        private val COST_BUDGET = doublePreferencesKey("cost_budget")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val anthropicApiKey: Flow<String> = context.dataStore.data.map { it[ANTHROPIC_API_KEY] ?: "" }
    val anthropicBaseUrl: Flow<String> = context.dataStore.data.map { it[ANTHROPIC_BASE_URL] ?: "https://api.anthropic.com/" }
    val anthropicModel: Flow<String> = context.dataStore.data.map { it[ANTHROPIC_MODEL] ?: "claude-haiku-4-5-20251001" }

    val openaiApiKey: Flow<String> = context.dataStore.data.map { it[OPENAI_API_KEY] ?: "" }
    val openaiBaseUrl: Flow<String> = context.dataStore.data.map { it[OPENAI_BASE_URL] ?: "https://api.openai.com/" }
    val openaiModel: Flow<String> = context.dataStore.data.map { it[OPENAI_MODEL] ?: "gpt-4o-mini" }

    val serverUrl: Flow<String> = context.dataStore.data.map { it[SERVER_URL] ?: "http://localhost:3000" }
    val executorPreference: Flow<String> = context.dataStore.data.map { it[EXECUTOR_PREFERENCE] ?: "AUTO" }
    val costBudget: Flow<Double> = context.dataStore.data.map { it[COST_BUDGET] ?: 1.0 }
    val themeMode: Flow<String> = context.dataStore.data.map { it[THEME_MODE] ?: "SYSTEM" }

    suspend fun saveAnthropicApiKey(key: String) = context.dataStore.edit { it[ANTHROPIC_API_KEY] = key }
    suspend fun saveAnthropicBaseUrl(url: String) = context.dataStore.edit { it[ANTHROPIC_BASE_URL] = url }
    suspend fun saveAnthropicModel(model: String) = context.dataStore.edit { it[ANTHROPIC_MODEL] = model }

    suspend fun saveOpenaiApiKey(key: String) = context.dataStore.edit { it[OPENAI_API_KEY] = key }
    suspend fun saveOpenaiBaseUrl(url: String) = context.dataStore.edit { it[OPENAI_BASE_URL] = url }
    suspend fun saveOpenaiModel(model: String) = context.dataStore.edit { it[OPENAI_MODEL] = model }

    suspend fun saveServerUrl(url: String) = context.dataStore.edit { it[SERVER_URL] = url }
    suspend fun saveExecutorPreference(preference: String) = context.dataStore.edit { it[EXECUTOR_PREFERENCE] = preference }
    suspend fun saveCostBudget(budget: Double) = context.dataStore.edit { it[COST_BUDGET] = budget }
    suspend fun saveThemeMode(theme: String) = context.dataStore.edit { it[THEME_MODE] = theme }

    // 批量保存所有设置，避免多次 edit 导致的并发问题
    suspend fun saveAllSettings(
        anthropicKey: String,
        anthropicBaseUrl: String,
        anthropicModel: String,
        openaiKey: String,
        openaiBaseUrl: String,
        openaiModel: String,
        executorPref: String,
        costBudget: Double,
        themeMode: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[ANTHROPIC_API_KEY] = anthropicKey
            prefs[ANTHROPIC_BASE_URL] = anthropicBaseUrl
            prefs[ANTHROPIC_MODEL] = anthropicModel
            prefs[OPENAI_API_KEY] = openaiKey
            prefs[OPENAI_BASE_URL] = openaiBaseUrl
            prefs[OPENAI_MODEL] = openaiModel
            prefs[EXECUTOR_PREFERENCE] = executorPref
            prefs[COST_BUDGET] = costBudget
            prefs[THEME_MODE] = themeMode
        }
    }

    suspend fun clearAll() = context.dataStore.edit { it.clear() }
}

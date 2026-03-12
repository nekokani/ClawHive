package com.example.hybridagent.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridagent.data.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try { settingsDataStore.anthropicApiKey.collect { _uiState.update { s -> s.copy(anthropicApiKey = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.anthropicBaseUrl.collect { _uiState.update { s -> s.copy(anthropicBaseUrl = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.anthropicModel.collect { _uiState.update { s -> s.copy(anthropicModel = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.openaiApiKey.collect { _uiState.update { s -> s.copy(openaiApiKey = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.openaiBaseUrl.collect { _uiState.update { s -> s.copy(openaiBaseUrl = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.openaiModel.collect { _uiState.update { s -> s.copy(openaiModel = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.executorPreference.collect { pref ->
                val preference = try { ExecutorPreference.valueOf(pref) } catch (_: Exception) { ExecutorPreference.AUTO }
                _uiState.update { s -> s.copy(executorPreference = preference) }
            }} catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.costBudget.collect { _uiState.update { s -> s.copy(costBudget = it) } } } catch (_: Exception) {}
        }
        viewModelScope.launch {
            try { settingsDataStore.themeMode.collect { theme ->
                val mode = try { ThemeMode.valueOf(theme) } catch (_: Exception) { ThemeMode.SYSTEM }
                _uiState.update { s -> s.copy(themeMode = mode) }
            }} catch (_: Exception) {}
        }
    }

    fun updateApiKey(provider: ApiProvider, key: String) {
        _uiState.update {
            when (provider) {
                ApiProvider.ANTHROPIC -> it.copy(anthropicApiKey = key, hasUnsavedChanges = true)
                ApiProvider.OPENAI -> it.copy(openaiApiKey = key, hasUnsavedChanges = true)
            }
        }
    }

    fun updateAnthropicBaseUrl(url: String) {
        _uiState.update { it.copy(anthropicBaseUrl = url, hasUnsavedChanges = true) }
    }

    fun updateAnthropicModel(model: String) {
        _uiState.update { it.copy(anthropicModel = model, hasUnsavedChanges = true) }
    }

    fun updateOpenaiBaseUrl(url: String) {
        _uiState.update { it.copy(openaiBaseUrl = url, hasUnsavedChanges = true) }
    }

    fun updateOpenaiModel(model: String) {
        _uiState.update { it.copy(openaiModel = model, hasUnsavedChanges = true) }
    }

    fun updateExecutorPreference(preference: ExecutorPreference) {
        _uiState.update { it.copy(executorPreference = preference, hasUnsavedChanges = true) }
    }

    fun updateCostBudget(budget: Double) {
        _uiState.update { it.copy(costBudget = budget, hasUnsavedChanges = true) }
    }

    fun updateTheme(theme: ThemeMode) {
        _uiState.update { it.copy(themeMode = theme, hasUnsavedChanges = true) }
    }

    fun saveSettings() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, saveError = null) }
                val s = _uiState.value
                settingsDataStore.saveAnthropicApiKey(s.anthropicApiKey)
                settingsDataStore.saveAnthropicBaseUrl(s.anthropicBaseUrl)
                settingsDataStore.saveAnthropicModel(s.anthropicModel)
                settingsDataStore.saveOpenaiApiKey(s.openaiApiKey)
                settingsDataStore.saveOpenaiBaseUrl(s.openaiBaseUrl)
                settingsDataStore.saveOpenaiModel(s.openaiModel)
                settingsDataStore.saveExecutorPreference(s.executorPreference.name)
                settingsDataStore.saveCostBudget(s.costBudget)
                settingsDataStore.saveThemeMode(s.themeMode.name)
                _uiState.update { it.copy(isSaving = false, hasUnsavedChanges = false, saveSuccess = true) }
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(saveSuccess = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveError = "保存失败: ${e.message}") }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                settingsDataStore.clearAll()
                _uiState.update { SettingsUiState() }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveError = "清除数据失败: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(saveError = null) }
    }
}

data class SettingsUiState(
    val anthropicApiKey: String = "",
    val anthropicBaseUrl: String = "https://api.anthropic.com/",
    val anthropicModel: String = "claude-haiku-4-5-20251001",
    val openaiApiKey: String = "",
    val openaiBaseUrl: String = "https://api.openai.com/",
    val openaiModel: String = "gpt-4o-mini",
    val executorPreference: ExecutorPreference = ExecutorPreference.AUTO,
    val costBudget: Double = 1.0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appVersion: String = "1.0.0",
    val hasUnsavedChanges: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)

enum class ApiProvider { ANTHROPIC, OPENAI }
enum class ExecutorPreference { AUTO, LOCAL_FIRST, CLOUD_FIRST }
enum class ThemeMode { LIGHT, DARK, SYSTEM }

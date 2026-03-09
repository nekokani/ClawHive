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
            settingsDataStore.anthropicApiKey.collect { key ->
                _uiState.update { it.copy(anthropicApiKey = key) }
            }
        }
        viewModelScope.launch {
            settingsDataStore.openaiApiKey.collect { key ->
                _uiState.update { it.copy(openaiApiKey = key) }
            }
        }
        viewModelScope.launch {
            settingsDataStore.serverUrl.collect { url ->
                _uiState.update { it.copy(serverUrl = url) }
            }
        }
        viewModelScope.launch {
            settingsDataStore.executorPreference.collect { pref ->
                _uiState.update {
                    it.copy(executorPreference = ExecutorPreference.valueOf(pref))
                }
            }
        }
        viewModelScope.launch {
            settingsDataStore.costBudget.collect { budget ->
                _uiState.update { it.copy(costBudget = budget) }
            }
        }
        viewModelScope.launch {
            settingsDataStore.themeMode.collect { theme ->
                _uiState.update {
                    it.copy(themeMode = ThemeMode.valueOf(theme))
                }
            }
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

    fun updateServerUrl(url: String) {
        _uiState.update { it.copy(serverUrl = url, hasUnsavedChanges = true) }
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

                val state = _uiState.value
                settingsDataStore.saveAnthropicApiKey(state.anthropicApiKey)
                settingsDataStore.saveOpenaiApiKey(state.openaiApiKey)
                settingsDataStore.saveServerUrl(state.serverUrl)
                settingsDataStore.saveExecutorPreference(state.executorPreference.name)
                settingsDataStore.saveCostBudget(state.costBudget)
                settingsDataStore.saveThemeMode(state.themeMode.name)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        hasUnsavedChanges = false,
                        saveSuccess = true
                    )
                }

                // 清除成功提示
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(saveSuccess = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveError = "保存失败: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                settingsDataStore.clearAll()
                _uiState.update { SettingsUiState() }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(saveError = "清除数据失败: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(saveError = null) }
    }
}

data class SettingsUiState(
    val anthropicApiKey: String = "",
    val openaiApiKey: String = "",
    val serverUrl: String = "http://localhost:3000",
    val executorPreference: ExecutorPreference = ExecutorPreference.AUTO,
    val costBudget: Double = 1.0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appVersion: String = "1.0.0",
    val hasUnsavedChanges: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null
)

enum class ApiProvider {
    ANTHROPIC,
    OPENAI
}

enum class ExecutorPreference {
    AUTO,
    LOCAL_FIRST,
    CLOUD_FIRST
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

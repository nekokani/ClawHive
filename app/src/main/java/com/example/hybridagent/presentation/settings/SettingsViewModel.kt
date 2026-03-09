package com.example.hybridagent.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // TODO: Inject DataStore or SharedPreferences for settings persistence
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateApiKey(provider: ApiProvider, key: String) {
        viewModelScope.launch {
            when (provider) {
                ApiProvider.ANTHROPIC -> {
                    _uiState.update { it.copy(anthropicApiKey = key) }
                    // TODO: Save to DataStore
                }
                ApiProvider.OPENAI -> {
                    _uiState.update { it.copy(openaiApiKey = key) }
                    // TODO: Save to DataStore
                }
            }
        }
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(serverUrl = url) }
            // TODO: Save to DataStore
        }
    }

    fun updateExecutorPreference(preference: ExecutorPreference) {
        viewModelScope.launch {
            _uiState.update { it.copy(executorPreference = preference) }
            // TODO: Save to DataStore
        }
    }

    fun updateCostBudget(budget: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(costBudget = budget) }
            // TODO: Save to DataStore
        }
    }

    fun updateTheme(theme: ThemeMode) {
        viewModelScope.launch {
            _uiState.update { it.copy(themeMode = theme) }
            // TODO: Save to DataStore
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            // TODO: Clear all local data
            _uiState.update { SettingsUiState() }
        }
    }
}

data class SettingsUiState(
    val anthropicApiKey: String = "",
    val openaiApiKey: String = "",
    val serverUrl: String = "https://api.example.com",
    val executorPreference: ExecutorPreference = ExecutorPreference.AUTO,
    val costBudget: Double = 1.0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appVersion: String = "1.0.0"
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

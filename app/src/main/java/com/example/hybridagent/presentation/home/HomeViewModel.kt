package com.example.hybridagent.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridagent.data.local.SettingsDataStore
import com.example.hybridagent.data.model.ChatMessage
import com.example.hybridagent.data.repository.LlmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val llmRepository: LlmRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        val userMessage = ChatMessage(role = "user", content = userInput)
        val updatedMessages = _uiState.value.messages + userMessage

        _uiState.update { it.copy(messages = updatedMessages, isLoading = true, error = null) }

        viewModelScope.launch {
            val anthropicKey = settingsDataStore.anthropicApiKey.first()
            val anthropicBaseUrl = settingsDataStore.anthropicBaseUrl.first()
            val anthropicModel = settingsDataStore.anthropicModel.first()
            val openaiKey = settingsDataStore.openaiApiKey.first()
            val openaiBaseUrl = settingsDataStore.openaiBaseUrl.first()
            val openaiModel = settingsDataStore.openaiModel.first()

            val result = when {
                anthropicKey.isNotBlank() -> llmRepository.chatWithClaude(
                    apiKey = anthropicKey,
                    messages = updatedMessages,
                    baseUrl = anthropicBaseUrl,
                    model = anthropicModel
                )
                openaiKey.isNotBlank() -> llmRepository.chatWithOpenAi(
                    apiKey = openaiKey,
                    messages = updatedMessages,
                    baseUrl = openaiBaseUrl,
                    model = openaiModel
                )
                else -> Result.failure(Exception("请先在设置中填写 API Key（Anthropic 或 OpenAI）"))
            }

            result.onSuccess { reply ->
                val assistantMessage = ChatMessage(role = "assistant", content = reply)
                _uiState.update {
                    it.copy(messages = it.messages + assistantMessage, isLoading = false)
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message ?: "请求失败") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(messages = emptyList(), error = null) }
    }
}

data class HomeUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

package com.example.hybridagent.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridagent.data.local.ChatMessageDao
import com.example.hybridagent.data.local.SettingsDataStore
import com.example.hybridagent.data.model.ChatMessage
import com.example.hybridagent.data.model.toChatMessage
import com.example.hybridagent.data.model.toEntity
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
    private val settingsDataStore: SettingsDataStore,
    private val chatMessageDao: ChatMessageDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // 延迟加载历史记录，避免初始化时崩溃
        viewModelScope.launch {
            try {
                loadChatHistory()
            } catch (e: Exception) {
                Log.e("ClawHive", "Failed to load chat history in init", e)
            }
        }
    }

    private suspend fun loadChatHistory() {
        try {
            val messages = chatMessageDao.getAllMessagesList().map { it.toChatMessage() }
            _uiState.update { it.copy(messages = messages) }
            Log.d("ClawHive", "Loaded ${messages.size} messages from database")
        } catch (e: Exception) {
            Log.e("ClawHive", "Failed to load chat history", e)
            // 不抛出异常，使用空列表
            _uiState.update { it.copy(messages = emptyList()) }
        }
    }

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        val userMessage = ChatMessage(role = "user", content = userInput)
        val updatedMessages = _uiState.value.messages + userMessage

        _uiState.update { it.copy(messages = updatedMessages, isLoading = true, error = null) }

        // 异步保存用户消息到数据库
        viewModelScope.launch {
            try {
                chatMessageDao.insertMessage(userMessage.toEntity())
                Log.d("ClawHive", "Saved user message to database")
            } catch (e: Exception) {
                Log.e("ClawHive", "Failed to save user message", e)
                // 不影响发送流程
            }
        }

        viewModelScope.launch {
            try {
                val anthropicKey = settingsDataStore.anthropicApiKey.first()
                val anthropicBaseUrl = settingsDataStore.anthropicBaseUrl.first()
                val anthropicModel = settingsDataStore.anthropicModel.first()
                val openaiKey = settingsDataStore.openaiApiKey.first()
                val openaiBaseUrl = settingsDataStore.openaiBaseUrl.first()
                val openaiModel = settingsDataStore.openaiModel.first()

                Log.d("ClawHive", "Anthropic key exists: ${anthropicKey.isNotBlank()}, OpenAI key exists: ${openaiKey.isNotBlank()}")

                val result = when {
                    anthropicKey.isNotBlank() -> {
                        Log.d("ClawHive", "Using Anthropic API")
                        llmRepository.chatWithClaude(
                            apiKey = anthropicKey,
                            messages = updatedMessages,
                            baseUrl = anthropicBaseUrl,
                            model = anthropicModel
                        )
                    }
                    openaiKey.isNotBlank() -> {
                        Log.d("ClawHive", "Using OpenAI API")
                        llmRepository.chatWithOpenAi(
                            apiKey = openaiKey,
                            messages = updatedMessages,
                            baseUrl = openaiBaseUrl,
                            model = openaiModel
                        )
                    }
                    else -> {
                        Log.w("ClawHive", "No API key configured")
                        Result.failure(Exception("请先在设置中填写 API Key（Anthropic 或 OpenAI）"))
                    }
                }

                result.onSuccess { reply ->
                    Log.d("ClawHive", "Received reply: ${reply.take(50)}...")
                    val assistantMessage = ChatMessage(role = "assistant", content = reply)
                    _uiState.update {
                        it.copy(messages = it.messages + assistantMessage, isLoading = false)
                    }
                    // 异步保存 AI 回复到数据库
                    viewModelScope.launch {
                        try {
                            chatMessageDao.insertMessage(assistantMessage.toEntity())
                            Log.d("ClawHive", "Saved assistant message to database")
                        } catch (e: Exception) {
                            Log.e("ClawHive", "Failed to save assistant message", e)
                        }
                    }
                }.onFailure { error ->
                    Log.e("ClawHive", "Chat error", error)
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "请求失败") }
                }
            } catch (e: Exception) {
                Log.e("ClawHive", "sendMessage exception", e)
                _uiState.update { it.copy(isLoading = false, error = "发送失败: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearMessages() {
        viewModelScope.launch {
            try {
                chatMessageDao.clearAll()
                _uiState.update { it.copy(messages = emptyList(), error = null) }
                Log.d("ClawHive", "Cleared all messages")
            } catch (e: Exception) {
                Log.e("ClawHive", "Failed to clear messages", e)
                _uiState.update { it.copy(error = "清空失败: ${e.message}") }
            }
        }
    }
}

data class HomeUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

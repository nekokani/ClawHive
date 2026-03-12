package com.example.hybridagent.data.repository

import com.example.hybridagent.data.model.AnthropicRequest
import com.example.hybridagent.data.model.ChatMessage
import com.example.hybridagent.data.model.OpenAiRequest
import com.example.hybridagent.data.remote.ClaudeApiService
import com.example.hybridagent.data.remote.OpenAiApiService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class LlmRepository @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    private fun buildClaudeService(baseUrl: String): ClaudeApiService {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ClaudeApiService::class.java)
    }

    private fun buildOpenAiService(baseUrl: String): OpenAiApiService {
        val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OpenAiApiService::class.java)
    }

    suspend fun chatWithClaude(
        apiKey: String,
        messages: List<ChatMessage>,
        baseUrl: String = "https://api.anthropic.com/",
        model: String = "claude-haiku-4-5-20251001"
    ): Result<String> {
        return try {
            val service = buildClaudeService(baseUrl)
            val response = service.chat(
                apiKey = apiKey,
                version = "2023-06-01",
                request = AnthropicRequest(
                    model = model,
                    max_tokens = 4096,
                    messages = messages
                )
            )
            if (response.isSuccessful) {
                val text = response.body()?.content
                    ?.firstOrNull { it.type == "text" }?.text
                    ?: "（空回复）"
                Result.success(text)
            } else {
                val errBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("Claude API 错误 ${response.code()}: $errBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Claude 请求失败: ${e.message}"))
        }
    }

    suspend fun chatWithOpenAi(
        apiKey: String,
        messages: List<ChatMessage>,
        baseUrl: String = "https://api.openai.com/",
        model: String = "gpt-4o-mini"
    ): Result<String> {
        return try {
            val service = buildOpenAiService(baseUrl)
            val response = service.chat(
                authorization = "Bearer $apiKey",
                request = OpenAiRequest(
                    model = model,
                    messages = messages
                )
            )
            if (response.isSuccessful) {
                val text = response.body()?.choices
                    ?.firstOrNull()?.message?.content
                    ?: "（空回复）"
                Result.success(text)
            } else {
                val errBody = response.errorBody()?.string() ?: ""
                Result.failure(Exception("OpenAI API 错误 ${response.code()}: $errBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("OpenAI 请求失败: ${e.message}"))
        }
    }
}

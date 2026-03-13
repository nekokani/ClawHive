package com.example.hybridagent.data.repository

import android.util.Log
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
        return try {
            val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ClaudeApiService::class.java)
        } catch (e: Exception) {
            Log.e("ClawHive", "Failed to build Claude service", e)
            throw e
        }
    }

    private fun buildOpenAiService(baseUrl: String): OpenAiApiService {
        return try {
            val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(OpenAiApiService::class.java)
        } catch (e: Exception) {
            Log.e("ClawHive", "Failed to build OpenAI service", e)
            throw e
        }
    }

    suspend fun chatWithClaude(
        apiKey: String,
        messages: List<ChatMessage>,
        baseUrl: String = "https://api.anthropic.com/",
        model: String = "claude-haiku-4-5-20251001"
    ): Result<String> {
        return try {
            Log.d("ClawHive", "Calling Claude API")
            Log.d("ClawHive", "  Base URL: $baseUrl")
            Log.d("ClawHive", "  Model: $model")
            Log.d("ClawHive", "  API Key length: ${apiKey.length}")
            Log.d("ClawHive", "  Messages count: ${messages.size}")

            val service = buildClaudeService(baseUrl)
            val response = service.chat(
                apiKey = apiKey,
                version = "2023-06-01",  // Anthropic API version
                request = AnthropicRequest(
                    model = model,
                    max_tokens = 4096,
                    messages = messages
                )
            )

            Log.d("ClawHive", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val text = response.body()?.content
                    ?.firstOrNull { it.type == "text" }?.text
                    ?: "（空回复）"
                Log.d("ClawHive", "Claude response success: ${text.take(100)}")
                Result.success(text)
            } else {
                val errBody = response.errorBody()?.string() ?: ""
                Log.e("ClawHive", "Claude API error ${response.code()}: $errBody")

                // 提供更详细的错误信息
                val errorMessage = when (response.code()) {
                    401 -> "API Key 无效或未授权"
                    403 -> "访问被拒绝。请检查：\n1. API Key 是否正确\n2. API Key 是否有权限访问此模型\n3. Base URL 是否正确"
                    404 -> "API 端点不存在，请检查 Base URL"
                    429 -> "请求过于频繁，请稍后重试"
                    else -> "API 错误 ${response.code()}"
                }

                Result.failure(Exception("$errorMessage\n详细信息: $errBody"))
            }
        } catch (e: Exception) {
            Log.e("ClawHive", "Claude request failed", e)
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
            Log.d("ClawHive", "Calling OpenAI API: $baseUrl, model: $model")
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
                Log.d("ClawHive", "OpenAI response success: ${text.take(100)}")
                Result.success(text)
            } else {
                val errBody = response.errorBody()?.string() ?: ""
                Log.e("ClawHive", "OpenAI API error ${response.code()}: $errBody")
                Result.failure(Exception("OpenAI API 错误 ${response.code()}: $errBody"))
            }
        } catch (e: Exception) {
            Log.e("ClawHive", "OpenAI request failed", e)
            Result.failure(Exception("OpenAI 请求失败: ${e.message}"))
        }
    }
}

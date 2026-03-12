package com.example.hybridagent.data.remote

import com.example.hybridagent.data.model.AnthropicRequest
import com.example.hybridagent.data.model.AnthropicResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ClaudeApiService {
    @POST("v1/messages")
    suspend fun chat(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String,
        @Body request: AnthropicRequest
    ): Response<AnthropicResponse>
}

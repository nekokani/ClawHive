package com.example.hybridagent.data.remote

import com.example.hybridagent.data.model.OpenAiRequest
import com.example.hybridagent.data.model.OpenAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApiService {
    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: OpenAiRequest
    ): Response<OpenAiResponse>
}

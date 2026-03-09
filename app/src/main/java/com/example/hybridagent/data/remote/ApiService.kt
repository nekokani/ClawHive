package com.example.hybridagent.data.remote

import com.example.hybridagent.data.model.TaskRequest
import com.example.hybridagent.data.model.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/v1/tasks/execute")
    suspend fun executeTask(
        @Body request: TaskRequest
    ): Response<TaskResponse>

    @GET("api/v1/tasks/history")
    suspend fun getHistory(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<List<TaskResponse>>
}

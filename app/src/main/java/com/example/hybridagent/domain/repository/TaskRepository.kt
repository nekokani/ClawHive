package com.example.hybridagent.domain.repository

import com.example.hybridagent.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun executeTask(task: Task): Result<Task>
    suspend fun getHistory(limit: Int, offset: Int): Result<List<Task>>
    suspend fun getTaskById(id: String): Result<Task>
    suspend fun deleteTask(id: String): Result<Unit>
    fun getHistoryFlow(): Flow<List<Task>>
}

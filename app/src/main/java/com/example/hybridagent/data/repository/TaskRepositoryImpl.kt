package com.example.hybridagent.data.repository

import com.example.hybridagent.data.local.TaskDao
import com.example.hybridagent.data.model.TaskEntity
import com.example.hybridagent.data.model.TaskInput
import com.example.hybridagent.data.model.TaskRequest
import com.example.hybridagent.data.remote.ApiService
import com.example.hybridagent.domain.model.ExecutorType
import com.example.hybridagent.domain.model.Task
import com.example.hybridagent.domain.model.TaskStatus
import com.example.hybridagent.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val taskDao: TaskDao
) : TaskRepository {

    override suspend fun executeTask(task: Task): Result<Task> {
        return try {
            taskDao.insert(task.toEntity())

            val response = apiService.executeTask(
                TaskRequest(
                    task = TaskInput(
                        type = task.type.name.lowercase(),
                        content = task.content
                    )
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!.result
                val completedTask = task.copy(
                    status = TaskStatus.COMPLETED,
                    result = result.output,
                    executor = ExecutorType.valueOf(result.executor.uppercase()),
                    cost = result.cost,
                    completedAt = System.currentTimeMillis()
                )

                taskDao.update(completedTask.toEntity())
                Result.success(completedTask)
            } else {
                val failedTask = task.copy(status = TaskStatus.FAILED)
                taskDao.update(failedTask.toEntity())
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            val failedTask = task.copy(status = TaskStatus.FAILED)
            taskDao.update(failedTask.toEntity())
            Result.failure(e)
        }
    }

    override suspend fun getHistory(limit: Int, offset: Int): Result<List<Task>> {
        return try {
            val localTasks = taskDao.getAll(limit, offset).map { it.toTask() }
            Result.success(localTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskById(id: String): Result<Task> {
        return try {
            val task = taskDao.getById(id)?.toTask()
            if (task != null) {
                Result.success(task)
            } else {
                Result.failure(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(id: String): Result<Unit> {
        return try {
            taskDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getHistoryFlow(): Flow<List<Task>> {
        return taskDao.getAllFlow().map { entities ->
            entities.map { it.toTask() }
        }
    }
}

private fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        content = content,
        type = type.name,
        status = status.name,
        executor = executor.name,
        result = result,
        cost = cost,
        createdAt = createdAt,
        completedAt = completedAt
    )
}

private fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        content = content,
        type = com.example.hybridagent.domain.model.TaskType.valueOf(type),
        status = TaskStatus.valueOf(status),
        executor = ExecutorType.valueOf(executor),
        result = result,
        cost = cost,
        createdAt = createdAt,
        completedAt = completedAt
    )
}

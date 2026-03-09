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
            // 先保存任务到数据库
            taskDao.insert(task.toEntity())

            // 尝试调用 API
            try {
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
                    val executorType = try {
                        ExecutorType.valueOf(result.executor.uppercase())
                    } catch (_: Exception) {
                        ExecutorType.UNKNOWN
                    }
                    val completedTask = task.copy(
                        status = TaskStatus.COMPLETED,
                        result = result.output,
                        executor = executorType,
                        cost = result.cost,
                        completedAt = System.currentTimeMillis()
                    )

                    taskDao.update(completedTask.toEntity())
                    Result.success(completedTask)
                } else {
                    // API 调用失败
                    try {
                        val failedTask = task.copy(status = TaskStatus.FAILED)
                        taskDao.update(failedTask.toEntity())
                    } catch (dbError: Exception) {
                        // 忽略数据库更新错误
                    }
                    Result.failure(Exception("OpenClaw 服务暂不可用 (HTTP ${response.code()})"))
                }
            } catch (networkError: Exception) {
                // 网络错误（连接失败、超时等）
                try {
                    val failedTask = task.copy(status = TaskStatus.FAILED)
                    taskDao.update(failedTask.toEntity())
                } catch (dbError: Exception) {
                    // 忽略数据库更新错误
                }

                // 根据异常类型返回友好的错误消息
                val errorMessage = when {
                    networkError is java.net.ConnectException ->
                        "OpenClaw 服务暂不可用：无法连接到服务器"
                    networkError is java.net.SocketTimeoutException ->
                        "OpenClaw 服务暂不可用：连接超时"
                    networkError is java.net.UnknownHostException ->
                        "OpenClaw 服务暂不可用：无法解析服务器地址"
                    else ->
                        "OpenClaw 服务暂不可用：${networkError.message ?: "网络错误"}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // 数据库插入失败或其他未预期的错误
            Result.failure(Exception("任务创建失败：${e.message ?: "未知错误"}"))
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

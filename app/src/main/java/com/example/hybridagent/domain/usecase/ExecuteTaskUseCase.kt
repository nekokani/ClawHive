package com.example.hybridagent.domain.usecase

import com.example.hybridagent.domain.model.Task
import com.example.hybridagent.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExecuteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(task: Task): Flow<Result<Task>> = flow {
        try {
            emit(repository.executeTask(task))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

package com.example.hybridagent.domain.usecase

import com.example.hybridagent.domain.model.Task
import com.example.hybridagent.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(limit: Int = 20, offset: Int = 0): Flow<Result<List<Task>>> = flow {
        emit(repository.getHistory(limit, offset))
    }
}

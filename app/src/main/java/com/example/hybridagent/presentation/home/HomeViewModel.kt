package com.example.hybridagent.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridagent.domain.model.Task
import com.example.hybridagent.domain.model.TaskStatus
import com.example.hybridagent.domain.model.TaskType
import com.example.hybridagent.domain.model.ExecutorType
import com.example.hybridagent.domain.usecase.ExecuteTaskUseCase
import com.example.hybridagent.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val executeTaskUseCase: ExecuteTaskUseCase,
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun executeTask(content: String, type: TaskType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val task = Task(
                content = content,
                type = type,
                status = TaskStatus.PENDING,
                executor = ExecutorType.UNKNOWN
            )
            
            executeTaskUseCase(task).collect { result ->
                result.onSuccess { completedTask ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            currentTask = completedTask,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
            }
        }
    }
    
    fun loadHistory() {
        viewModelScope.launch {
            getHistoryUseCase(limit = 10, offset = 0).collect { result ->
                result.onSuccess { tasks ->
                    _uiState.update { it.copy(recentTasks = tasks) }
                }
            }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val currentTask: Task? = null,
    val recentTasks: List<Task> = emptyList(),
    val error: String? = null
)

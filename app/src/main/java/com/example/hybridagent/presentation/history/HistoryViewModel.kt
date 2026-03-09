package com.example.hybridagent.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hybridagent.domain.model.Task
import com.example.hybridagent.domain.model.ExecutorType
import com.example.hybridagent.domain.usecase.GetHistoryUseCase
import com.example.hybridagent.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                getHistoryUseCase(limit = 100, offset = 0).collect { result ->
                    result.onSuccess { tasks ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                allTasks = tasks,
                                filteredTasks = filterTasks(tasks, it.searchQuery, it.selectedFilter),
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                allTasks = emptyList(),
                                filteredTasks = emptyList(),
                                error = "加载失败: ${error.message ?: "数据库访问错误"}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allTasks = emptyList(),
                        filteredTasks = emptyList(),
                        error = "加载失败: ${e.message ?: "未知错误"}"
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredTasks = filterTasks(it.allTasks, query, it.selectedFilter)
            )
        }
    }

    fun onFilterChange(filter: TaskFilter) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                filteredTasks = filterTasks(it.allTasks, it.searchQuery, filter)
            )
        }
    }

    fun toggleTaskSelection(taskId: String) {
        _uiState.update {
            val newSelection = if (it.selectedTaskIds.contains(taskId)) {
                it.selectedTaskIds - taskId
            } else {
                it.selectedTaskIds + taskId
            }
            it.copy(selectedTaskIds = newSelection)
        }
    }

    fun deleteSelectedTasks() {
        viewModelScope.launch {
            try {
                _uiState.value.selectedTaskIds.forEach { taskId ->
                    taskRepository.deleteTask(taskId)
                }
                _uiState.update { it.copy(selectedTaskIds = emptySet()) }
                loadHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "删除失败: ${e.message ?: "未知错误"}")
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
                loadHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "删除失败: ${e.message ?: "未知错误"}")
                }
            }
        }
    }

    fun toggleFavorite(taskId: String) {
        _uiState.update {
            val newFavorites = if (it.favoriteTaskIds.contains(taskId)) {
                it.favoriteTaskIds - taskId
            } else {
                it.favoriteTaskIds + taskId
            }
            it.copy(favoriteTaskIds = newFavorites)
        }
    }

    private fun filterTasks(
        tasks: List<Task>,
        query: String,
        filter: TaskFilter
    ): List<Task> {
        var filtered = tasks

        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.content.contains(query, ignoreCase = true) ||
                it.result?.contains(query, ignoreCase = true) == true
            }
        }

        filtered = when (filter) {
            TaskFilter.ALL -> filtered
            TaskFilter.LOCAL -> filtered.filter { it.executor == ExecutorType.LOCAL }
            TaskFilter.CLOUD -> filtered.filter { 
                it.executor == ExecutorType.CLAUDE || it.executor == ExecutorType.GPT 
            }
            TaskFilter.FAVORITES -> filtered.filter { 
                _uiState.value.favoriteTaskIds.contains(it.id) 
            }
        }

        return filtered.sortedByDescending { it.createdAt }
    }
}

data class HistoryUiState(
    val isLoading: Boolean = false,
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val selectedTaskIds: Set<String> = emptySet(),
    val favoriteTaskIds: Set<String> = emptySet(),
    val error: String? = null
)

enum class TaskFilter {
    ALL,
    LOCAL,
    CLOUD,
    FAVORITES
}

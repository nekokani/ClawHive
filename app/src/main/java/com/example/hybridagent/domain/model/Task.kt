package com.example.hybridagent.domain.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: TaskType,
    val status: TaskStatus,
    val executor: ExecutorType,
    val result: String? = null,
    val cost: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

enum class TaskType {
    SIMPLE_QUERY,
    FILE_OPERATION,
    COMPLEX_REASONING,
    CODE_GENERATION,
    CREATIVE_WRITING
}

enum class TaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}

enum class ExecutorType {
    LOCAL,
    CLAUDE,
    GPT,
    UNKNOWN
}

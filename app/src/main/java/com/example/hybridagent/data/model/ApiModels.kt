package com.example.hybridagent.data.model

data class TaskRequest(
    val task: TaskInput,
    val options: TaskOptions? = null
)

data class TaskInput(
    val type: String,
    val content: String,
    val context: Map<String, Any>? = null
)

data class TaskOptions(
    val preferLocal: Boolean = false,
    val maxCost: Double = 0.5
)

data class TaskResponse(
    val success: Boolean,
    val result: TaskResult
)

data class TaskResult(
    val output: String,
    val executor: String,
    val model: String?,
    val cost: Double,
    val executionTime: Double
)

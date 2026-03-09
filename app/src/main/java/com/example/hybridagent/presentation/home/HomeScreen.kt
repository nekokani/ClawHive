package com.example.hybridagent.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hybridagent.domain.model.Task
import com.example.hybridagent.domain.model.TaskType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onTaskClick: (Task) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var taskInput by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hybrid Agent") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, "历史")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "设置")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 输入框
            OutlinedTextField(
                value = taskInput,
                onValueChange = { taskInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("输入你的任务...") },
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* 语音输入 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Mic, "语音")
                    Spacer(Modifier.width(4.dp))
                    Text("语音")
                }
                
                Button(
                    onClick = { /* 附件 */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AttachFile, "附件")
                    Spacer(Modifier.width(4.dp))
                    Text("附件")
                }
                
                Button(
                    onClick = {
                        if (taskInput.isNotBlank()) {
                            viewModel.executeTask(
                                taskInput,
                                TaskType.COMPLEX_REASONING
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = taskInput.isNotBlank() && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, "执行")
                        Spacer(Modifier.width(4.dp))
                        Text("执行")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 快捷操作
            Text(
                "快捷操作",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip("💻 代码分析") {
                    taskInput = "分析这段代码的性能瓶颈"
                }
                QuickActionChip("✍️ 写作") {
                    taskInput = "帮我写一篇关于..."
                }
                QuickActionChip("🔍 搜索") {
                    taskInput = "搜索最新的..."
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 最近任务
            Text(
                "最近任务",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(uiState.recentTasks) { task ->
                    TaskItem(
                        task = task,
                        onClick = { onTaskClick(task) }
                    )
                }
            }
            
            // 错误提示
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun QuickActionChip(
    text: String,
    onClick: () -> Unit
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    task.content.take(50) + if (task.content.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    task.executor.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "成本: $${String.format("%.4f", task.cost)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.example.hybridagent.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // API 配置
            SettingsSection(title = "API 配置") {
                ApiKeyField(
                    label = "Anthropic API Key",
                    value = uiState.anthropicApiKey,
                    onValueChange = { viewModel.updateApiKey(ApiProvider.ANTHROPIC, it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ApiKeyField(
                    label = "OpenAI API Key",
                    value = uiState.openaiApiKey,
                    onValueChange = { viewModel.updateApiKey(ApiProvider.OPENAI, it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.serverUrl,
                    onValueChange = viewModel::updateServerUrl,
                    label = { Text("服务器地址") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Cloud, "服务器")
                    },
                    singleLine = true
                )
            }

            // 执行器偏好
            SettingsSection(title = "执行器偏好") {
                ExecutorPreference.values().forEach { preference ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.executorPreference == preference,
                            onClick = { viewModel.updateExecutorPreference(preference) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                when (preference) {
                                    ExecutorPreference.AUTO -> "自动选择"
                                    ExecutorPreference.LOCAL_FIRST -> "本地优先"
                                    ExecutorPreference.CLOUD_FIRST -> "云端优先"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                when (preference) {
                                    ExecutorPreference.AUTO -> "根据任务复杂度自动选择"
                                    ExecutorPreference.LOCAL_FIRST -> "优先使用本地 OpenClaw"
                                    ExecutorPreference.CLOUD_FIRST -> "优先使用云端 AI"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 成本预算
            SettingsSection(title = "成本预算") {
                var budgetText by remember(uiState.costBudget) {
                    mutableStateOf(uiState.costBudget.toString())
                }

                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { newValue ->
                        budgetText = newValue
                        newValue.toDoubleOrNull()?.let { budget ->
                            viewModel.updateCostBudget(budget)
                        }
                    },
                    label = { Text("单次任务最大成本 (USD)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, "成本")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "超过此预算的任务将被拒绝执行",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 主题设置
            SettingsSection(title = "主题") {
                ThemeMode.values().forEach { theme ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.themeMode == theme,
                            onClick = { viewModel.updateTheme(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when (theme) {
                                ThemeMode.LIGHT -> "浅色模式"
                                ThemeMode.DARK -> "深色模式"
                                ThemeMode.SYSTEM -> "跟随系统"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // 关于
            SettingsSection(title = "关于") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "应用版本",
                    subtitle = uiState.appVersion
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsItem(
                    icon = Icons.Default.Code,
                    title = "开源许可",
                    subtitle = "查看开源许可信息"
                )
            }

            // 数据管理
            SettingsSection(title = "数据管理") {
                Button(
                    onClick = { showClearDataDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.DeleteForever, "清除数据")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("清除所有数据")
                }
            }
        }
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = { Icon(Icons.Default.Warning, "警告") },
            title = { Text("确认清除数据") },
            text = { Text("此操作将清除所有历史记录和设置，且无法恢复。确定要继续吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("清除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun ApiKeyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    if (isVisible) "隐藏" else "显示"
                )
            }
        },
        leadingIcon = {
            Icon(Icons.Default.Key, "API Key")
        },
        singleLine = true
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

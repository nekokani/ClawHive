# Hybrid Agent Android App - 产品需求文档 (PRD)

**版本**: 1.0  
**日期**: 2026-03-09  
**状态**: Draft

---

## 1. 产品概述

### 1.1 产品定位
一款混合智能 Agent 移动应用，结合本地 OpenClaw agent 能力和云端大模型增强，为用户提供智能任务分析、规划和执行服务。

### 1.2 核心价值
- **智能路由**: 自动判断任务复杂度，选择最优执行方式（本地/云端）
- **成本优化**: 简单任务本地处理，复杂任务云端增强，降低 API 成本
- **移动优先**: 随时随地访问 AI 助手能力
- **隐私保护**: 敏感操作本地执行，数据不上传

### 1.3 目标用户
- **开发者**: 需要移动端访问 AI 编程助手
- **知识工作者**: 需要 AI 辅助分析、写作、决策
- **技术爱好者**: 对 AI agent 技术感兴趣的用户

---

## 2. 系统架构

### 2.1 整体架构

```
┌─────────────────────────────────────┐
│     Android App (客户端)             │
│  - Jetpack Compose UI               │
│  - MVVM Architecture                │
│  - Retrofit + OkHttp                │
└──────────────┬──────────────────────┘
               │ HTTPS/REST API
┌──────────────▼──────────────────────┐
│   Backend Server (Node.js)          │
│  - Task Router                      │
│  - OpenClaw Integrator              │
│  - Cloud AI Adapter                 │
└──────────────┬──────────────────────┘
               │
       ┌───────┴────────┐
       │                │
┌──────▼─────┐   ┌─────▼──────┐
│ OpenClaw   │   │ Cloud AI   │
│ (本地)      │   │ (云端)      │
└────────────┘   └────────────┘
```

### 2.2 技术选型

**Android 客户端**:
- **语言**: Kotlin 1.9+
- **UI 框架**: Jetpack Compose (Material 3)
- **架构模式**: MVVM + Clean Architecture
- **网络库**: Retrofit 2.9+ + OkHttp 4.12+
- **异步处理**: Kotlin Coroutines + Flow
- **依赖注入**: Hilt (Dagger)
- **本地存储**: Room Database + DataStore
- **最低 SDK**: Android 8.0 (API 26)
- **目标 SDK**: Android 14 (API 34)

**后端服务**:
- **语言**: Node.js 18+ / TypeScript
- **框架**: Express.js / Fastify
- **数据库**: SQLite / PostgreSQL
- **缓存**: Redis (可选)

---

## 3. 功能需求

### 3.1 核心功能

#### F1: 任务执行
**优先级**: P0  
**描述**: 用户输入任务，系统自动路由并执行

**用户故事**:
```
作为用户
我想要输入一个任务描述
系统能够自动判断并执行
以便我快速获得结果
```

**验收标准**:
- [ ] 支持文本输入（最长 10000 字符）
- [ ] 支持语音输入（转文字）
- [ ] 自动任务分类（简单/复杂）
- [ ] 显示执行进度
- [ ] 显示执行结果
- [ ] 显示成本信息（云端调用时）

#### F2: 历史记录
**优先级**: P0  
**描述**: 查看和管理历史任务

**功能点**:
- 按时间倒序显示历史任务
- 支持搜索和筛选
- 支持删除单条/批量删除
- 支持收藏重要任务
- 显示每条任务的执行器类型和成本

#### F3: 设置与配置
**优先级**: P1  
**描述**: 配置 API 密钥和偏好设置

**功能点**:
- API 密钥管理（Anthropic、OpenAI）
- 后端服务器地址配置
- 默认执行器偏好（本地优先/云端优先）
- 成本预算设置
- 主题切换（浅色/深色/跟随系统）

#### F4: 实时流式输出
**优先级**: P1  
**描述**: 云端模型支持流式响应

**功能点**:
- SSE (Server-Sent Events) 接收流式数据
- 实时显示生成内容
- 支持中断生成

#### F5: 多模态输入
**优先级**: P2  
**描述**: 支持图片、文件上传

**功能点**:
- 拍照/相册选择图片
- 文件选择器（支持常见格式）
- 图片预览和编辑
- 文件大小限制（10MB）

### 3.2 非功能需求

#### NFR1: 性能
- 应用启动时间 < 2秒
- 任务提交响应时间 < 500ms
- 流式输出延迟 < 100ms
- 内存占用 < 200MB

#### NFR2: 安全
- API 密钥本地加密存储（Android Keystore）
- HTTPS 通信
- 证书固定（Certificate Pinning）
- 敏感数据不记录日志

#### NFR3: 可用性
- 支持离线查看历史记录
- 网络异常时友好提示
- 支持深色模式
- 支持无障碍功能

#### NFR4: 兼容性
- 支持 Android 8.0+
- 支持多种屏幕尺寸（手机/平板）
- 支持横竖屏切换

---

## 4. UI/UX 设计

### 4.1 页面结构

```
App
├── 主页 (HomeScreen)
│   ├── 输入框
│   ├── 快捷操作按钮
│   └── 最近任务列表
├── 历史 (HistoryScreen)
│   ├── 搜索栏
│   ├── 筛选器
│   └── 任务列表
├── 设置 (SettingsScreen)
│   ├── API 配置
│   ├── 偏好设置
│   └── 关于
└── 任务详情 (TaskDetailScreen)
    ├── 任务信息
    ├── 执行结果
    └── 操作按钮
```

### 4.2 设计原则
- **Material 3 Design**: 遵循 Google 最新设计规范
- **响应式布局**: 适配不同屏幕尺寸
- **无障碍**: 支持 TalkBack、大字体
- **动画流畅**: 使用 Compose 动画 API

---

## 5. 技术实现细节

### 5.1 Android 项目结构

```
app/
├── src/main/
│   ├── java/com/example/hybridagent/
│   │   ├── data/
│   │   │   ├── model/          # 数据模型
│   │   │   ├── repository/     # 数据仓库
│   │   │   ├── local/          # 本地数据源（Room）
│   │   │   └── remote/         # 远程数据源（Retrofit）
│   │   ├── domain/
│   │   │   ├── model/          # 领域模型
│   │   │   ├── repository/     # 仓库接口
│   │   │   └── usecase/        # 用例
│   │   ├── presentation/
│   │   │   ├── home/           # 主页
│   │   │   ├── history/        # 历史
│   │   │   ├── settings/       # 设置
│   │   │   └── common/         # 通用组件
│   │   ├── di/                 # 依赖注入
│   │   └── util/               # 工具类
│   └── res/                    # 资源文件
└── build.gradle.kts
```

### 5.2 核心类设计

#### 5.2.1 数据模型

```kotlin
// Task.kt
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
    LOCAL,      // OpenClaw 本地
    CLAUDE,     // Claude API
    GPT,        // OpenAI GPT
    UNKNOWN
}
```

#### 5.2.2 API 接口

```kotlin
// ApiService.kt
interface ApiService {
    @POST("api/v1/tasks/execute")
    suspend fun executeTask(
        @Body request: TaskRequest
    ): Response<TaskResponse>
    
    @POST("api/v1/tasks/execute")
    fun executeTaskStream(
        @Body request: TaskRequest
    ): Flow<ServerSentEvent>
    
    @GET("api/v1/tasks/history")
    suspend fun getHistory(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<List<Task>>
}

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
```

#### 5.2.3 Repository

```kotlin
// TaskRepository.kt
interface TaskRepository {
    suspend fun executeTask(task: Task): Result<Task>
    fun executeTaskStream(task: Task): Flow<String>
    suspend fun getHistory(limit: Int, offset: Int): Result<List<Task>>
    suspend fun getTaskById(id: String): Result<Task>
    suspend fun deleteTask(id: String): Result<Unit>
    suspend fun saveTask(task: Task): Result<Unit>
}

class TaskRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val taskDao: TaskDao
) : TaskRepository {
    
    override suspend fun executeTask(task: Task): Result<Task> {
        return try {
            // 保存到本地数据库
            taskDao.insert(task.toEntity())
            
            // 调用后端 API
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
                
                // 更新本地数据库
                taskDao.update(completedTask.toEntity())
                
                Result.success(completedTask)
            } else {
                Result.failure(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun executeTaskStream(task: Task): Flow<String> = flow {
        apiService.executeTaskStream(
            TaskRequest(
                task = TaskInput(
                    type = task.type.name.lowercase(),
                    content = task.content
                )
            )
        ).collect { event ->
            emit(event.data)
        }
    }
    
    override suspend fun getHistory(limit: Int, offset: Int): Result<List<Task>> {
        return try {
            // 优先从本地数据库读取
            val localTasks = taskDao.getAll(limit, offset).map { it.toTask() }
            Result.success(localTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### 5.2.4 ViewModel

```kotlin
// HomeViewModel.kt
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
```

#### 5.2.5 Compose UI

```kotlin
// HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var taskInput by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hybrid Agent") },
                actions = {
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
                    TaskItem(task = task)
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

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
```

### 5.3 依赖配置

```kotlin
// build.gradle.kts (app)
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

---

## 6. API 接口规范

### 6.1 执行任务

**请求**:
```http
POST /api/v1/tasks/execute
Content-Type: application/json

{
  "task": {
    "type": "complex_reasoning",
    "content": "分析这段代码的性能瓶颈",
    "context": {
      "code": "...",
      "language": "python"
    }
  },
  "options": {
    "preferLocal": false,
    "maxCost": 0.1
  }
}
```

**响应**:
```json
{
  "success": true,
  "result": {
    "output": "分析结果...",
    "executor": "claude",
    "model": "claude-opus-4",
    "cost": 0.05,
    "executionTime": 2.3
  }
}
```

### 6.2 流式执行

**请求**:
```http
POST /api/v1/tasks/execute
Content-Type: application/json
Accept: text/event-stream

{
  "task": {
    "type": "creative_writing",
    "content": "写一篇关于 AI 的文章"
  }
}
```

**响应** (SSE):
```
data: {"chunk": "人工智能"}

data: {"chunk": "（AI）是"}

data: {"chunk": "计算机科学"}

data: {"done": true, "cost": 0.03}
```

### 6.3 获取历史

**请求**:
```http
GET /api/v1/tasks/history?limit=20&offset=0
```

**响应**:
```json
{
  "tasks": [
    {
      "id": "task-123",
      "content": "分析代码性能",
      "type": "complex_reasoning",
      "status": "completed",
      "executor": "claude",
      "result": "...",
      "cost": 0.05,
      "createdAt": 1709971200000,
      "completedAt": 1709971205000
    }
  ],
  "total": 100
}
```

---

## 7. 开发计划

### 7.1 里程碑

#### Phase 1: MVP (4周)
- [ ] 基础 UI 框架（Compose + Navigation）
- [ ] 任务执行核心功能
- [ ] 后端 API 集成
- [ ] 本地数据库（Room）
- [ ] 基础设置页面

#### Phase 2: 增强功能 (3周)
- [ ] 流式输出支持
- [ ] 历史记录搜索和筛选
- [ ] 成本统计和预算控制
- [ ] 深色模式
- [ ] 错误处理和重试机制

#### Phase 3: 高级功能 (3周)
- [ ] 语音输入
- [ ] 图片/文件上传
- [ ] 离线模式
- [ ] 性能优化
- [ ] 单元测试和 UI 测试

#### Phase 4: 发布准备 (2周)
- [ ] 安全审计
- [ ] 性能测试
- [ ] 用户测试
- [ ] 文档完善
- [ ] Google Play 上架

### 7.2 技术风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 后端 API 不稳定 | 高 | 实现重试机制、降级策略 |
| 流式输出实现复杂 | 中 | 使用成熟的 SSE 库 |
| 成本控制不准确 | 中 | 实时监控、预算告警 |
| 性能问题 | 中 | 性能测试、代码优化 |

---

## 8. 成功指标

### 8.1 技术指标
- 应用崩溃率 < 0.5%
- API 成功率 > 99%
- 平均响应时间 < 2秒
- 内存占用 < 200MB

### 8.2 业务指标
- 日活用户 (DAU)
- 任务执行成功率
- 用户留存率（7日、30日）
- 平均每用户任务数

### 8.3 用户满意度
- 应用商店评分 > 4.5
- 用户反馈响应时间 < 24小时
- NPS (Net Promoter Score) > 50

---

## 9. 附录

### 9.1 参考资料
- [Android 官方文档](https://developer.android.com/)
- [Jetpack Compose 指南](https://developer.android.com/jetpack/compose)
- [Material 3 设计规范](https://m3.material.io/)
- [Retrofit 文档](https://square.github.io/retrofit/)
- [Hilt 依赖注入](https://developer.android.com/training/dependency-injection/hilt-android)

### 9.2 术语表
- **MVVM**: Model-View-ViewModel 架构模式
- **SSE**: Server-Sent Events，服务器推送事件
- **PRD**: Product Requirements Document，产品需求文档
- **API**: Application Programming Interface，应用程序接口
- **SDK**: Software Development Kit，软件开发工具包

---

**文档结束**

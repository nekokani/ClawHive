# Hybrid Agent Android App - 产品需求文档 (PRD) v1.1

**版本**: 1.1  
**日期**: 2026-03-09  
**状态**: Ready for Development  
**变更**: 修复架构不清晰、技术细节缺失等问题

---

## 📋 变更日志

### v1.1 (2026-03-09)
- ✅ 明确架构：Android 客户端 + Node.js 后端服务
- ✅ 移除 OpenClaw 依赖（简化实现）
- ✅ 明确 API 密钥管理方案
- ✅ 补充 SSE 实现技术方案
- ✅ 补充语音输入技术方案
- ✅ 明确后端服务部署方案
- ✅ 补充开发环境配置说明

---

## 1. 产品概述

### 1.1 产品定位
一款混合智能 Agent 移动应用，结合本地轻量级处理和云端大模型增强，为用户提供智能任务分析、规划和执行服务。

### 1.2 核心价值
- **智能路由**: 自动判断任务复杂度，选择最优执行方式（本地/云端）
- **成本优化**: 简单任务本地处理，复杂任务云端增强，降低 API 成本
- **移动优先**: 随时随地访问 AI 助手能力
- **隐私保护**: 敏感操作本地处理，数据不上传

### 1.3 目标用户
- **开发者**: 需要移动端访问 AI 编程助手
- **知识工作者**: 需要 AI 辅助分析、写作、决策
- **技术爱好者**: 对 AI agent 技术感兴趣的用户

---

## 2. 系统架构

### 2.1 整体架构（简化版）

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
│  - Express.js API                   │
│  - Task Router (任务路由)            │
│  - Local Processor (本地处理器)      │
│  - Cloud AI Adapter (云端适配器)     │
└──────────────┬──────────────────────┘
               │
       ┌───────┴────────┐
       │                │
┌──────▼─────┐   ┌─────▼──────┐
│ 本地处理    │   │ Cloud AI   │
│ (简单任务)  │   │ (复杂任务)  │
│ - 文本处理  │   │ - Claude   │
│ - 模板生成  │   │ - GPT-4    │
└────────────┘   └────────────┘
```

### 2.2 架构说明

**关键变更**: 移除 OpenClaw 依赖，简化为：
- **本地处理器**: 后端服务器上的简单逻辑处理（非 AI）
- **云端 AI**: 调用 Claude/GPT 等大模型 API

**为什么这样设计**:
1. OpenClaw 是复杂的 agent 框架，不适合移动场景
2. 简单任务用规则引擎处理即可，无需本地 AI
3. 复杂任务直接调用云端 API，效果更好

### 2.3 技术选型

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
- **框架**: Express.js
- **数据库**: SQLite (开发) / PostgreSQL (生产)
- **缓存**: 内存缓存（可选 Redis）
- **部署**: Docker 容器

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
**描述**: 配置后端服务器和偏好设置

**功能点**:
- 后端服务器地址配置
- 默认执行器偏好（本地优先/云端优先）
- 主题切换（浅色/深色/跟随系统）
- 关于页面

**注意**: API 密钥在后端服务器配置，不在 Android 端

#### F4: 实时流式输出
**优先级**: P1  
**描述**: 云端模型支持流式响应

**功能点**:
- SSE (Server-Sent Events) 接收流式数据
- 实时显示生成内容
- 支持中断生成

**技术方案**: 使用 OkHttp EventSource 库

#### F5: 语音输入（可选）
**优先级**: P2  
**描述**: 支持语音转文字

**技术方案**: 使用 Android Speech Recognizer API

### 3.2 非功能需求

#### NFR1: 性能
- 应用启动时间 < 2秒
- 任务提交响应时间 < 500ms
- 流式输出延迟 < 100ms
- 内存占用 < 200MB

#### NFR2: 安全
- HTTPS 通信
- 后端 API 认证（Bearer Token）
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
│   ├── 服务器配置
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


### 5.2 后端项目结构

```
backend/
├── src/
│   ├── routes/
│   │   └── tasks.ts            # API 路由
│   ├── services/
│   │   ├── TaskRouter.ts       # 任务路由器
│   │   ├── LocalProcessor.ts   # 本地处理器
│   │   └── CloudAIAdapter.ts   # 云端 AI 适配器
│   ├── models/
│   │   └── Task.ts             # 数据模型
│   ├── utils/
│   │   ├── logger.ts           # 日志
│   │   └── config.ts           # 配置
│   └── index.ts                # 入口文件
├── config/
│   └── default.yaml            # 配置文件
├── package.json
├── tsconfig.json
└── Dockerfile
```

### 5.3 部署方案

**开发环境**:
- 后端运行在本地 `http://localhost:3000`
- Android 模拟器访问 `http://10.0.2.2:3000`
- 真机访问电脑局域网 IP

**生产环境**:
- 后端部署到云服务器（AWS/阿里云/腾讯云）
- 使用 Docker 容器化部署
- 配置 HTTPS 证书
- Android 端配置生产服务器地址

---

## 6. API 接口规范

### 6.1 执行任务

**请求**:
```http
POST /api/v1/tasks/execute
Content-Type: application/json
Authorization: Bearer <token>

{
  "content": "分析这段代码的性能瓶颈",
  "type": "auto",
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
  "task": {
    "id": "task-123",
    "content": "分析这段代码的性能瓶颈",
    "type": "complex_reasoning",
    "status": "completed",
    "executor": "claude",
    "model": "claude-sonnet-4",
    "result": "分析结果...",
    "cost": 0.05,
    "executionTime": 2.3,
    "createdAt": 1709971200000,
    "completedAt": 1709971205000
  }
}
```

### 6.2 流式执行

**请求**:
```http
POST /api/v1/tasks/execute/stream
Content-Type: application/json
Accept: text/event-stream
Authorization: Bearer <token>

{
  "content": "写一篇关于 AI 的文章",
  "type": "creative_writing"
}
```

**响应** (SSE):
```
data: {"type":"start","taskId":"task-456"}

data: {"type":"chunk","content":"人工智能"}

data: {"type":"chunk","content":"（AI）是"}

data: {"type":"done","cost":0.03,"executionTime":5.2}
```

### 6.3 获取历史

**请求**:
```http
GET /api/v1/tasks/history?limit=20&offset=0
Authorization: Bearer <token>
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

#### Phase 1: 后端 MVP (1周)
- [ ] Express.js 基础框架
- [ ] 任务路由器实现
- [ ] 本地处理器（简单规则）
- [ ] Claude/GPT API 集成
- [ ] SQLite 数据库
- [ ] Docker 配置

#### Phase 2: Android MVP (2周)
- [ ] Compose UI 框架
- [ ] 任务执行核心功能
- [ ] 后端 API 集成
- [ ] Room 本地数据库
- [ ] 基础设置页面

#### Phase 3: 增强功能 (2周)
- [ ] 流式输出支持
- [ ] 历史记录搜索和筛选
- [ ] 成本统计
- [ ] 深色模式
- [ ] 错误处理和重试

#### Phase 4: 发布准备 (1周)
- [ ] 安全审计
- [ ] 性能测试
- [ ] 用户测试
- [ ] 文档完善

### 7.2 技术风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 后端 API 不稳定 | 高 | 实现重试机制、降级策略 |
| 流式输出实现复杂 | 中 | 使用成熟的 SSE 库 |
| 成本控制不准确 | 中 | 实时监控、预算告警 |
| 性能问题 | 中 | 性能测试、代码优化 |

---

## 8. 环境配置说明

### 8.1 后端环境变量

创建 `.env` 文件：
```bash
# 服务器配置
PORT=3000
NODE_ENV=development

# API 密钥（重要：不要提交到 Git）
ANTHROPIC_API_KEY=sk-ant-xxx
OPENAI_API_KEY=sk-xxx

# 数据库
DATABASE_URL=sqlite:./data/hybrid-agent.db

# 认证
JWT_SECRET=your-secret-key
```

### 8.2 Android 配置

在 `local.properties` 添加：
```properties
# 开发环境后端地址
backend.url.debug=http://10.0.2.2:3000

# 生产环境后端地址
backend.url.release=https://your-server.com
```

---

## 9. 成功指标

### 9.1 技术指标
- 应用崩溃率 < 0.5%
- API 成功率 > 99%
- 平均响应时间 < 2秒
- 内存占用 < 200MB

### 9.2 业务指标
- 日活用户 (DAU)
- 任务执行成功率
- 用户留存率（7日、30日）
- 平均每用户任务数

---

## 10. 附录

### 10.1 参考资料
- [Android 官方文档](https://developer.android.com/)
- [Jetpack Compose 指南](https://developer.android.com/jetpack/compose)
- [Material 3 设计规范](https://m3.material.io/)
- [Retrofit 文档](https://square.github.io/retrofit/)
- [Express.js 文档](https://expressjs.com/)

### 10.2 术语表
- **MVVM**: Model-View-ViewModel 架构模式
- **SSE**: Server-Sent Events，服务器推送事件
- **PRD**: Product Requirements Document，产品需求文档
- **API**: Application Programming Interface，应用程序接口
- **SDK**: Software Development Kit，软件开发工具包

---

**文档结束**


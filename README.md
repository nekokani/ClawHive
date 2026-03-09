# ClawHive 🐝

**ClawHive** - 混合智能 Agent Android 应用

一款结合本地 OpenClaw agent 能力和云端大模型增强的智能移动应用，为用户提供智能任务分析、规划和执行服务。

## 项目概述

ClawHive 是一个创新的混合架构 AI Agent 应用，灵感来源于蜂群（Swarm）的协作模式和 OpenClaw 的强大能力。应用通过智能路由系统，自动判断任务复杂度，选择最优执行方式（本地/云端），实现成本优化和性能最大化。

### 核心特性

- 🤖 **智能路由**: 自动判断任务复杂度，选择最优执行方式
- 💰 **成本优化**: 简单任务本地处理，复杂任务云端增强
- 📱 **移动优先**: 随时随地访问 AI 助手能力
- 🔒 **隐私保护**: 敏感操作本地执行，数据不上传
- 🎨 **Material 3 设计**: 遵循 Google 最新设计规范
- ⚡ **实时流式输出**: 支持云端模型流式响应

## 技术架构

### Android 客户端
- **语言**: Kotlin 1.9+
- **UI 框架**: Jetpack Compose (Material 3)
- **架构模式**: MVVM + Clean Architecture
- **网络库**: Retrofit 2.9+ + OkHttp 4.12+
- **异步处理**: Kotlin Coroutines + Flow
- **依赖注入**: Hilt (Dagger)
- **本地存储**: Room Database + DataStore
- **最低 SDK**: Android 8.0 (API 26)
- **目标 SDK**: Android 14 (API 34)

### 后端服务
- **语言**: Node.js 18+ / TypeScript
- **框架**: Express.js / Fastify
- **数据库**: SQLite / PostgreSQL
- **缓存**: Redis (可选)

### 系统架构图

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

## 功能特性

### 已实现功能 ✅

- [x] 基础 UI 框架（Jetpack Compose + Material 3）
- [x] MVVM 架构设计
- [x] 任务执行核心功能
- [x] 历史记录管理
- [x] 设置页面
- [x] 本地数据库（Room）
- [x] 网络请求封装（Retrofit）
- [x] 依赖注入（Hilt）

### 开发中功能 🚧

- [ ] 后端 API 服务
- [ ] OpenClaw 集成
- [ ] 云端 AI 适配器（Claude/GPT）
- [ ] 流式输出支持
- [ ] 语音输入
- [ ] 图片/文件上传
- [ ] 成本统计和预算控制

### 计划功能 📋

- [ ] 多模态输入（图片、语音、文件）
- [ ] 离线模式
- [ ] 任务队列系统
- [ ] 性能优化
- [ ] 单元测试和 UI 测试
- [ ] 深色模式完善
- [ ] 国际化支持

## 快速开始

### 环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17 或更高版本
- Android SDK 34
- Gradle 8.2+

### 构建步骤

1. **克隆仓库**
   ```bash
   git clone git@github.com:nekokani/ClawHive.git
   cd ClawHive
   ```

2. **配置 Android SDK**
   
   创建 `local.properties` 文件：
   ```properties
   sdk.dir=/path/to/your/android-sdk
   ```

3. **构建项目**
   ```bash
   ./gradlew assembleDebug
   ```

4. **安装到设备**
   ```bash
   ./gradlew installDebug
   ```

### 配置后端服务

后端服务配置请参考 [hybrid-agent-app-spec.md](hybrid-agent-app-spec.md)

## 项目结构

```
ClawHive/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/hybridagent/
│   │   │   ├── data/              # 数据层
│   │   │   │   ├── model/         # 数据模型
│   │   │   │   ├── repository/    # 数据仓库
│   │   │   │   ├── local/         # 本地数据源（Room）
│   │   │   │   └── remote/        # 远程数据源（Retrofit）
│   │   │   ├── domain/            # 领域层
│   │   │   │   ├── model/         # 领域模型
│   │   │   │   ├── repository/    # 仓库接口
│   │   │   │   └── usecase/       # 用例
│   │   │   ├── presentation/      # 表现层
│   │   │   │   ├── home/          # 主页
│   │   │   │   ├── history/       # 历史
│   │   │   │   ├── settings/      # 设置
│   │   │   │   ├── navigation/    # 导航
│   │   │   │   └── common/        # 通用组件
│   │   │   ├── di/                # 依赖注入
│   │   │   └── util/              # 工具类
│   │   └── res/                   # 资源文件
│   └── build.gradle.kts
├── docs/                          # 文档
├── gradle/
└── build.gradle.kts
```

## 开发规划

### Phase 1: MVP (已完成) ✅
- 基础 UI 框架
- 核心架构搭建
- 本地数据存储
- 基础功能实现

### Phase 2: 后端集成 (进行中) 🚧
**预计时间**: 2-3 周

- [ ] 搭建 Node.js 后端服务
- [ ] 实现任务路由器
- [ ] OpenClaw 集成
- [ ] 云端 AI 适配器（Claude/GPT）
- [ ] API 接口开发
- [ ] 前后端联调

### Phase 3: 功能增强 (计划中) 📋
**预计时间**: 3-4 周

- [ ] 流式输出支持
- [ ] 语音输入功能
- [ ] 图片/文件上传
- [ ] 成本统计和预算控制
- [ ] 历史记录搜索和筛选
- [ ] 错误处理和重试机制

### Phase 4: 优化与测试 (计划中) 📋
**预计时间**: 2-3 周

- [ ] 性能优化
- [ ] 内存优化
- [ ] 网络优化
- [ ] 单元测试
- [ ] UI 测试
- [ ] 集成测试

### Phase 5: 发布准备 (计划中) 📋
**预计时间**: 1-2 周

- [ ] 安全审计
- [ ] 性能测试
- [ ] 用户测试
- [ ] 文档完善
- [ ] Google Play 上架准备

## 贡献指南

欢迎贡献代码、报告问题或提出建议！

### 如何贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循 Kotlin 官方代码风格
- 使用有意义的变量和函数名
- 添加必要的注释
- 编写单元测试

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- **项目主页**: https://github.com/nekokani/ClawHive
- **问题反馈**: https://github.com/nekokani/ClawHive/issues

## 致谢

- [OpenClaw](https://openclaw.ai) - 提供强大的 Agent 能力
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代化的 Android UI 工具包
- [Material Design 3](https://m3.material.io/) - 设计系统

---

**ClawHive** - 让 AI 助手像蜂群一样协作 🐝✨

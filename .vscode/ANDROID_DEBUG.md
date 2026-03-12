# VS Code Android 调试配置说明

## 已配置内容

### 1. VS Code 扩展（需要安装）
打开 VS Code，按 `Cmd+Shift+P`，输入 "Extensions: Show Recommended Extensions"，安装：
- **Java Extension Pack** - Java 支持
- **Kotlin** - Kotlin 语言支持
- **Android Dev Extension** - Android 开发工具
- **Gradle for Java** - Gradle 构建支持

### 2. 调试配置 (.vscode/launch.json)
- **Android Launch** - 启动应用并附加调试器
- **Android Attach** - 附加到正在运行的应用

### 3. 任务配置 (.vscode/tasks.json)
按 `Cmd+Shift+P` → "Tasks: Run Task" 可执行：
- **Build Debug APK** - 构建调试版本
- **Install Debug APK** - 安装到设备
- **View Logcat** - 查看实时日志
- **Clear App Data** - 清除应用数据

### 4. 调试脚本 (debug_android.sh)
命令行工具，提供快捷操作：
```bash
./debug_android.sh
```

## 使用步骤

### 方式 1: VS Code 图形界面

1. **安装扩展**
   - 打开 VS Code
   - 左侧点击扩展图标
   - 搜索并安装推荐的扩展

2. **连接设备**
   - USB 连接 Android 设备或启动模拟器
   - 确保开启 USB 调试

3. **构建安装**
   - 按 `Cmd+Shift+B` 构建 APK
   - 按 `Cmd+Shift+P` → "Tasks: Run Task" → "Install Debug APK"

4. **查看日志**
   - 按 `Cmd+Shift+P` → "Tasks: Run Task" → "View Logcat"
   - 或使用终端：`adb logcat | grep ClawHive`

### 方式 2: 命令行脚本

```bash
# 交互式调试工具
./debug_android.sh

# 或直接使用 adb 命令
adb logcat -c                          # 清除日志
adb logcat | grep -E "ClawHive|FATAL"  # 实时查看
adb shell pm clear com.example.hybridagent  # 清除数据
```

### 方式 3: 快捷命令

```bash
# 构建并安装
./gradlew installDebug

# 查看日志（过滤 ClawHive）
adb logcat | grep ClawHive

# 清除应用数据
adb shell pm clear com.example.hybridagent

# 重启应用
adb shell am force-stop com.example.hybridagent
adb shell am start -n com.example.hybridagent/.MainActivity
```

## 调试技巧

### 1. 查看崩溃堆栈
```bash
adb logcat | grep -A 20 "FATAL EXCEPTION"
```

### 2. 过滤特定标签
```bash
adb logcat ClawHive:D AndroidRuntime:E *:S
```

### 3. 保存日志到文件
```bash
adb logcat -d > app_log.txt
```

### 4. 查看网络请求（OkHttp）
```bash
adb logcat | grep "OkHttp"
```

### 5. 查看数据库操作
```bash
adb logcat | grep "Room"
```

## 常见问题

### adb 命令找不到
需要安装 Android SDK platform-tools：
1. 打开 Android Studio
2. Tools → SDK Manager
3. SDK Tools 标签页
4. 勾选 "Android SDK Platform-Tools"
5. 点击 Apply

或添加到 PATH：
```bash
echo 'export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### 设备未授权
```bash
adb kill-server
adb start-server
# 在设备上点击"允许 USB 调试"
```

### 应用崩溃但看不到日志
```bash
# 清除旧日志后重新运行
adb logcat -c
# 启动应用
# 查看完整日志
adb logcat -d > full_log.txt
```

## 限制说明

⚠️ VS Code 的 Android 调试功能有限：
- ❌ 无法设置 Kotlin 代码断点
- ❌ 没有布局检查器
- ❌ 没有性能分析工具
- ✅ 可以查看 logcat 日志
- ✅ 可以构建和安装 APK

**推荐**：复杂调试仍建议使用 Android Studio。

# 编译错误修复指南

## 问题：Java 版本不匹配

当前错误：
```
Android Gradle plugin requires Java 17 to run. You are currently using Java 11.
```

## 解决方案

### 方式 1: 使用 Homebrew 安装 Java 17（推荐）

```bash
# 安装 Java 17
brew install openjdk@17

# 设置环境变量（临时）
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 或者永久设置（添加到 ~/.zshrc）
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# 验证
java -version
```

### 方式 2: 在 gradle.properties 中指定 Java 路径

如果你已经有 Java 17，可以在项目根目录的 `gradle.properties` 中添加：

```properties
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
```

### 方式 3: 使用 Android Studio 的内置 JDK

Android Studio 自带 JDK 17，可以在 `gradle.properties` 中指定：

```properties
# macOS
org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home
```

## 验证修复

```bash
# 清理并重新编译
./gradlew clean
./gradlew assembleDebug
```

## 当前项目要求

- **最低 Java 版本**: 17
- **Kotlin 版本**: 1.9.22
- **Gradle 版本**: 8.2
- **Android Gradle Plugin**: 8.x

## 快速修复命令

```bash
# 1. 安装 Java 17
brew install openjdk@17

# 2. 设置环境变量
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 3. 编译项目
./gradlew assembleDebug
```

## 如果使用 VS Code

在 `.vscode/settings.json` 中添加：

```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home",
      "default": true
    }
  ]
}
```

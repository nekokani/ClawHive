#!/bin/bash

# ClawHive Android 调试助手

set -e

PACKAGE_NAME="com.example.hybridagent"
ADB="${HOME}/Library/Android/sdk/platform-tools/adb"

# 检查 adb 是否存在
if [ ! -f "$ADB" ]; then
    echo "❌ adb 未找到，请先安装 Android SDK platform-tools"
    echo "可以通过 Android Studio SDK Manager 安装"
    exit 1
fi

# 检查设备连接
if ! $ADB devices | grep -q "device$"; then
    echo "❌ 未检测到 Android 设备或模拟器"
    echo "请确保设备已连接并开启 USB 调试"
    exit 1
fi

echo "🔧 ClawHive 调试工具"
echo "===================="
echo ""
echo "1. 构建并安装 APK"
echo "2. 查看实时日志"
echo "3. 清除应用数据"
echo "4. 重启应用"
echo "5. 导出崩溃日志"
echo "6. 查看应用信息"
echo "0. 退出"
echo ""
read -p "请选择操作 [0-6]: " choice

case $choice in
    1)
        echo "📦 构建 APK..."
        ./gradlew assembleDebug
        echo "📲 安装到设备..."
        ./gradlew installDebug
        echo "✅ 安装完成"
        ;;
    2)
        echo "📋 实时日志 (Ctrl+C 停止)..."
        $ADB logcat -c
        $ADB logcat | grep --color=always -E "ClawHive|AndroidRuntime|FATAL"
        ;;
    3)
        echo "🗑️  清除应用数据..."
        $ADB shell pm clear $PACKAGE_NAME
        echo "✅ 数据已清除"
        ;;
    4)
        echo "🔄 重启应用..."
        $ADB shell am force-stop $PACKAGE_NAME
        $ADB shell am start -n $PACKAGE_NAME/.MainActivity
        echo "✅ 应用已重启"
        ;;
    5)
        LOG_FILE="crash_log_$(date +%Y%m%d_%H%M%S).txt"
        echo "💾 导出崩溃日志到 $LOG_FILE..."
        $ADB logcat -d | grep -E "ClawHive|AndroidRuntime|FATAL" > "$LOG_FILE"
        echo "✅ 日志已保存: $LOG_FILE"
        ;;
    6)
        echo "ℹ️  应用信息:"
        $ADB shell dumpsys package $PACKAGE_NAME | grep -E "versionName|versionCode|targetSdk|minSdk"
        echo ""
        echo "📦 APK 路径:"
        $ADB shell pm path $PACKAGE_NAME
        echo ""
        echo "💾 数据目录:"
        $ADB shell run-as $PACKAGE_NAME pwd 2>/dev/null || echo "无法访问（需要 debuggable）"
        ;;
    0)
        echo "👋 再见"
        exit 0
        ;;
    *)
        echo "❌ 无效选择"
        exit 1
        ;;
esac

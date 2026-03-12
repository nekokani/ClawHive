#!/bin/bash

echo "==================================="
echo "ClawHive 崩溃日志捕获工具"
echo "==================================="
echo ""

# 检查设备连接
if ! adb devices | grep -q "device$"; then
    echo "❌ 错误: 没有检测到 Android 设备"
    echo "请确保:"
    echo "  1. 设备已通过 USB 连接"
    echo "  2. 已启用 USB 调试"
    echo "  3. 已授权此电脑进行调试"
    exit 1
fi

echo "✅ 设备已连接"
echo ""

# 清除旧日志
echo "清除旧日志..."
adb logcat -c

echo "开始监听日志..."
echo "请在设备上操作应用，重现崩溃"
echo "按 Ctrl+C 停止监听"
echo ""
echo "==================================="
echo ""

# 捕获日志
LOG_FILE="$HOME/Desktop/clawhive_crash_$(date +%Y%m%d_%H%M%S).log"

adb logcat -v time | tee "$LOG_FILE" | grep --line-buffered -E "ClawHive|HybridAgent|AndroidRuntime|FATAL|crash|Exception"

echo ""
echo "==================================="
echo "日志已保存到: $LOG_FILE"
echo "==================================="

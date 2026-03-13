package com.example.hybridagent

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltAndroidApp
class HybridAgentApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 全局异常处理 - 记录崩溃日志
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("ClawHive", "=== FATAL CRASH ===", throwable)
                Log.e("ClawHive", "Thread: ${thread.name}")
                Log.e("ClawHive", "Message: ${throwable.message}")

                // 保存崩溃日志到文件
                saveCrashLog(throwable)

                // 调用默认处理器
                defaultHandler?.uncaughtException(thread, throwable)
            } catch (e: Exception) {
                Log.e("ClawHive", "Error in crash handler", e)
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }

        Log.d("ClawHive", "Application started")
    }

    private fun saveCrashLog(throwable: Throwable) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            val logFile = File(getExternalFilesDir(null), "crash_$timestamp.log")

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)

            logFile.writeText("""
                ClawHive Crash Report
                Time: $timestamp

                Exception: ${throwable.javaClass.name}
                Message: ${throwable.message}

                Stack Trace:
                $sw
            """.trimIndent())

            Log.d("ClawHive", "Crash log saved to: ${logFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("ClawHive", "Failed to save crash log", e)
        }
    }
}

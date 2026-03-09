package com.example.hybridagent

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HybridAgentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 全局异常处理
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("ClawHive", "Uncaught exception in ${thread.name}", throwable)
        }
    }
}

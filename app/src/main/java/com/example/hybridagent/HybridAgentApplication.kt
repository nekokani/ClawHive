package com.example.hybridagent

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HybridAgentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}

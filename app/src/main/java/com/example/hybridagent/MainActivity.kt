package com.example.hybridagent

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.hybridagent.presentation.common.theme.HybridAgentTheme
import com.example.hybridagent.presentation.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            Log.d("ClawHive", "MainActivity onCreate started")
            
            setContent {
                HybridAgentTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
            
            Log.d("ClawHive", "MainActivity onCreate completed")
        } catch (e: Exception) {
            Log.e("ClawHive", "Error in MainActivity onCreate", e)
            throw e
        }
    }
}

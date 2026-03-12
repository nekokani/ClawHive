package com.example.hybridagent.util

import android.util.Log

object Logger {
    private const val TAG = "ClawHive"

    fun d(message: String, tag: String = TAG) {
        Log.d(tag, message)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }

    fun w(message: String, tag: String = TAG) {
        Log.w(tag, message)
    }
}

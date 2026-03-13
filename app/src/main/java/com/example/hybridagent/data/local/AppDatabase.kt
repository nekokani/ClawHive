package com.example.hybridagent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hybridagent.data.model.ChatMessageEntity
import com.example.hybridagent.data.model.TaskEntity

@Database(
    entities = [TaskEntity::class, ChatMessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun chatMessageDao(): ChatMessageDao
}

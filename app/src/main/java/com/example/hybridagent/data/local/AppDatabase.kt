package com.example.hybridagent.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hybridagent.data.model.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}

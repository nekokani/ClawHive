package com.example.hybridagent.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(role = role, content = content)
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        role = role,
        content = content
    )
}

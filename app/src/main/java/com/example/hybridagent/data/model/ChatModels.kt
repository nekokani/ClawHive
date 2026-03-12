package com.example.hybridagent.data.model

// --- Shared ---

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

// --- Anthropic ---

data class AnthropicRequest(
    val model: String,
    val max_tokens: Int,
    val messages: List<ChatMessage>
)

data class AnthropicResponse(
    val content: List<AnthropicContentBlock>,
    val usage: AnthropicUsage?
)

data class AnthropicContentBlock(
    val type: String,
    val text: String?
)

data class AnthropicUsage(
    val input_tokens: Int,
    val output_tokens: Int
)

// --- OpenAI ---

data class OpenAiRequest(
    val model: String,
    val messages: List<ChatMessage>
)

data class OpenAiResponse(
    val choices: List<OpenAiChoice>,
    val usage: OpenAiUsage?
)

data class OpenAiChoice(
    val message: ChatMessage
)

data class OpenAiUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int
)

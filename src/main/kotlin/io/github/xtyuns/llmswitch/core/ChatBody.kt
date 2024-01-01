package io.github.xtyuns.llmswitch.core

import com.fasterxml.jackson.annotation.JsonProperty

class ChatRequestBody(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false
)

class Message(
    val role: String,
    val content: String,
    val name: String? = null,
)

enum class MessageRole(val code: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool"),
}

class ChatResponseBody(
    val id: String,
    val choices: List<Choice>,
    val created: Int,
    val model: String,
    @JsonProperty("system_fingerprint")
    val systemFingerprint: String,
    val `object`: String,
    val usage: Usage,
)

class Choice(
    val index: Int,
    val message: Message,
    @JsonProperty("finish_reason")
    val finishReason: String,
    val logprobs: Any? = null,
)

class Usage(
    @JsonProperty("completion_tokens")
    val completionTokens: Int,
    @JsonProperty("prompt_tokens")
    val promptTokens: Int,
    @JsonProperty("total_tokens")
    val totalTokens: Int,
)
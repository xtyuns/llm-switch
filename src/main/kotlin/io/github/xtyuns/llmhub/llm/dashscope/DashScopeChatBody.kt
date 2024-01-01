package io.github.xtyuns.llmhub.llm.dashscope

import com.fasterxml.jackson.annotation.JsonProperty

class DashScopeChatRequestBody(
    val model: String,
    val input: DashScopeInput,
    val parameters: DashScopeParameters,
)

class DashScopeInput(
    val prompt: String? = null,
    val messages: List<DashScopeMessage>? = null,
)

class DashScopeMessage(
    val role: String,
    val content: String,
)

class DashScopeParameters(
    @JsonProperty("result_format")
    val resultFormat: String? = "message",
    val seed: Int? = null,
    @JsonProperty("max_tokens")
    val maxTokens: Int? = null,
    @JsonProperty("top_p")
    val topP: Float? = null,
    @JsonProperty("top_k")
    val topK: Int? = null,
    @JsonProperty("repetition_penalty")
    val repetitionPenalty: Float? = null,
    val temperature: Float? = null,
    val stop: List<String>? = null,
    @JsonProperty("enable_search")
    val enableSearch: Boolean? = null,
    @JsonProperty("incremental_output")
    val incrementalOutput: Boolean? = null,
)

class DashScopeChatResponseBody(
    @JsonProperty("request_id")
    val requestId: String,
    val output: DashScopeOutput,
    val usage: DashScopeUsage,
)

class DashScopeOutput(
    val text: String? = null,
    @JsonProperty("finish_reason")
    val finishReason: String? = null,
    val choices: List<DashScopeChoice>? = null,
)

class DashScopeChoice(
    val message: DashScopeMessage,
    @JsonProperty("finish_reason")
    val finishReason: String,
)

class DashScopeUsage(
    @JsonProperty("output_tokens")
    val outputTokens: Int,
    @JsonProperty("input_tokens")
    val inputTokens: Int,
    @JsonProperty("total_tokens")
    val totalTokens: Int,
)
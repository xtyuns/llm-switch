package io.github.xtyuns.llmswitch.core

enum class Scene(description: String) {
    CHAT("/v1/chat/completion 接口"),
    EMBEDDINGS("/v1/embeddings 接口"),
    OTHER("非标准接口场景"),
}
package io.github.xtyuns.llmswitch.llm.openai

import io.github.xtyuns.llmswitch.core.Brand
import org.springframework.stereotype.Component

@Component
class OpenAiBrand : Brand {
    override val name = "openai"
    override val requestCodec = OpenAiRequestCodec()
    override val responseCodec = OpenAiResponseCodec()
    override val defaultBaseUrl = "https://api.openai.com"
}
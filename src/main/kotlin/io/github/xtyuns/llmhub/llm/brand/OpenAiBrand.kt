package io.github.xtyuns.llmhub.llm.brand

import io.github.xtyuns.llmhub.core.Brand
import io.github.xtyuns.llmhub.llm.codec.OpenAiRequestCodec
import io.github.xtyuns.llmhub.llm.codec.OpenAiResponseCodec
import org.springframework.stereotype.Component

@Component
class OpenAiBrand : Brand {
    override val name = "openai"
    override val requestCodec = OpenAiRequestCodec()
    override val responseCodec = OpenAiResponseCodec()
    override val defaultBaseUrl = "https://api.openai.com"
}
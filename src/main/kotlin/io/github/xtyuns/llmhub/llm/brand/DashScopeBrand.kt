package io.github.xtyuns.llmhub.llm.brand

import io.github.xtyuns.llmhub.core.Brand
import io.github.xtyuns.llmhub.llm.codec.DashScopeRequestCodec
import io.github.xtyuns.llmhub.llm.codec.DashScopeResponseCodec
import org.springframework.stereotype.Component

@Component
class DashScopeBrand : Brand {
    override val name = "dashscope"
    override val requestCodec = DashScopeRequestCodec()
    override val responseCodec = DashScopeResponseCodec()
    override val defaultBaseUrl = "https://dashscope.aliyuncs.com"
}
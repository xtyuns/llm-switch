package io.github.xtyuns.llmswitch.llm.dashscope

import io.github.xtyuns.llmswitch.core.Brand
import org.springframework.stereotype.Component

@Component
class DashScopeBrand : Brand {
    override val name = "dashscope"
    override val requestCodec = DashScopeRequestCodec()
    override val responseCodec = DashScopeResponseCodec()
    override val defaultBaseUrl = "https://dashscope.aliyuncs.com"
}
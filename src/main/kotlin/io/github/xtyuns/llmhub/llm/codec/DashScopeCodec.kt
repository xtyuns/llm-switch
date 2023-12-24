package io.github.xtyuns.llmhub.llm.codec

import io.github.xtyuns.llmhub.core.RequestCodec
import io.github.xtyuns.llmhub.core.ResponseCodec

class DashScopeRequestCodec: RequestCodec() {
    override fun encode(data: String, apiPath: String): String {
        return data
    }

    override fun decode(data: String, apiPath: String): String {
        return data
    }
}

class DashScopeResponseCodec: ResponseCodec() {
    override fun encode(data: String, apiPath: String): String {
        return data
    }

    override fun decode(data: String, apiPath: String): String {
        return data
    }
}
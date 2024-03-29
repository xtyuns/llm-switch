package io.github.xtyuns.llmswitch.llm.openai

import io.github.xtyuns.llmswitch.core.CodedRequestBundle
import io.github.xtyuns.llmswitch.core.CodedResponseBundle
import io.github.xtyuns.llmswitch.core.RequestBundle
import io.github.xtyuns.llmswitch.core.RequestCodec
import io.github.xtyuns.llmswitch.core.ResponseBundle
import io.github.xtyuns.llmswitch.core.ResponseCodec
import io.github.xtyuns.llmswitch.core.Scene


class OpenAiRequestCodec : RequestCodec() {
    override fun encode(raw: RequestBundle): CodedRequestBundle {
        val scene = when (raw.requestPath) {
            "/v1/chat/completions" -> {
                Scene.CHAT
            }

            "/v1/embeddings" -> {
                Scene.EMBEDDINGS
            }

            else -> {
                Scene.OTHER
            }
        }

        return CodedRequestBundle(
            scene = scene,
            requestMethod = raw.requestMethod,
            requestPath = raw.requestPath,
            requestParameterMap = raw.requestParameterMap,
            requestHeaders = raw.requestHeaders,
            requestBody = raw.requestBody,
            rawRequestBundle = raw,
        )
    }

    override fun decode(coded: CodedRequestBundle): RequestBundle {
        return RequestBundle(
            requestMethod = coded.requestMethod,
            requestPath = coded.requestPath,
            requestParameterMap = coded.requestParameterMap,
            requestHeaders = coded.requestHeaders,
            requestBody = coded.requestBody,
            sourceCodedRequestBundle = coded,
        )
    }
}

class OpenAiResponseCodec : ResponseCodec() {
    override fun encode(raw: ResponseBundle): CodedResponseBundle {
        val scene = when (raw.requestBundle.requestPath) {
            "/v1/chat/completions" -> {
                Scene.CHAT
            }

            "/v1/embeddings" -> {
                Scene.EMBEDDINGS
            }

            else -> {
                Scene.OTHER
            }
        }

        return CodedResponseBundle(
            scene = scene,
            responseStatus = raw.responseStatus,
            responseHeaders = raw.responseHeaders,
            responseBody = raw.responseBody,
            rawResponseBundle = raw,
        )
    }

    override fun decode(coded: CodedResponseBundle): ResponseBundle {
        return ResponseBundle(
            responseStatus = coded.responseStatus,
            responseHeaders = coded.responseHeaders,
            responseBody = coded.responseBody,
            requestBundle = coded.rawResponseBundle.requestBundle.sourceCodedRequestBundle!!.rawRequestBundle,
        )
    }
}
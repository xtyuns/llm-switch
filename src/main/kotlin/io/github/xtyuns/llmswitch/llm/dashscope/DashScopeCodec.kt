package io.github.xtyuns.llmswitch.llm.dashscope

import io.github.xtyuns.llmhub.core.*
import io.github.xtyuns.llmswitch.utils.JsonUtils
import io.github.xtyuns.llmswitch.core.ChatRequestBody
import io.github.xtyuns.llmswitch.core.ChatResponseBody
import io.github.xtyuns.llmswitch.core.Choice
import io.github.xtyuns.llmswitch.core.CodedRequestBundle
import io.github.xtyuns.llmswitch.core.CodedResponseBundle
import io.github.xtyuns.llmswitch.core.Message
import io.github.xtyuns.llmswitch.core.MessageRole
import io.github.xtyuns.llmswitch.core.RequestBundle
import io.github.xtyuns.llmswitch.core.RequestCodec
import io.github.xtyuns.llmswitch.core.ResponseBundle
import io.github.xtyuns.llmswitch.core.ResponseCodec
import io.github.xtyuns.llmswitch.core.Scene
import io.github.xtyuns.llmswitch.core.Usage
import org.springframework.http.HttpMethod
import java.time.Instant
import java.util.*

class DashScopeRequestCodec : RequestCodec() {
    override fun encode(raw: RequestBundle): CodedRequestBundle {
        return when (raw.requestPath) {
            "/api/v1/services/aigc/text-generation/generation" -> {
                encodeChat(raw)
            }

            "/api/v1/services/embeddings/text-embedding/text-embedding" -> {
                encodeEmbeddings(raw)
            }

            else -> {
                CodedRequestBundle(
                    scene = Scene.OTHER,
                    requestMethod = raw.requestMethod,
                    requestPath = raw.requestPath,
                    requestParameterMap = raw.requestParameterMap,
                    requestHeaders = raw.requestHeaders,
                    requestBody = raw.requestBody,
                    rawRequestBundle = raw,
                )
            }
        }
    }

    private fun encodeEmbeddings(raw: RequestBundle): CodedRequestBundle {
        TODO()
    }

    private fun encodeChat(raw: RequestBundle): CodedRequestBundle {
        val dashScopeChatRequestBody = JsonUtils.parseJsonString(raw.requestBody, DashScopeChatRequestBody::class.java)

        val messages = (dashScopeChatRequestBody.input.messages ?: emptyList()).map {
            Message(it.role, it.content)
        }.toMutableList()
        if (dashScopeChatRequestBody.input.prompt != null) {
            messages.add(Message(MessageRole.USER.code, dashScopeChatRequestBody.input.prompt))
        }

        val chatRequestBody = ChatRequestBody(dashScopeChatRequestBody.model, messages)
        val requestBody = JsonUtils.toJsonString(chatRequestBody)
        return CodedRequestBundle(
            scene = Scene.CHAT,
            requestMethod = raw.requestMethod,
            requestPath = raw.requestPath,
            requestParameterMap = raw.requestParameterMap,
            requestHeaders = raw.requestHeaders,
            requestBody = requestBody,
            rawRequestBundle = raw,
        )
    }

    override fun decode(coded: CodedRequestBundle): RequestBundle {
        return when (coded.scene) {
            Scene.CHAT -> {
                decodeChat(coded)
            }

            Scene.EMBEDDINGS -> {
                decodeEmbeddings(coded)
            }

            else -> {
                RequestBundle(
                    requestMethod = coded.requestMethod,
                    requestPath = coded.requestPath,
                    requestParameterMap = coded.requestParameterMap,
                    requestHeaders = coded.requestHeaders,
                    requestBody = coded.requestBody,
                    sourceCodedRequestBundle = coded,
                )
            }
        }
    }

    private fun decodeEmbeddings(coded: CodedRequestBundle): RequestBundle {
        TODO()
    }

    private fun decodeChat(coded: CodedRequestBundle): RequestBundle {
        val chatRequestBody = JsonUtils.parseJsonString(coded.requestBody, ChatRequestBody::class.java)
        val dashScopeChatRequestBody = DashScopeChatRequestBody(
            chatRequestBody.model, DashScopeInput(
                messages = chatRequestBody.messages.map {
                    DashScopeMessage(it.role, it.content)
                },
            ), DashScopeParameters()
        )
        val requestBody = JsonUtils.toJsonString(dashScopeChatRequestBody)

        return RequestBundle(
            requestMethod = HttpMethod.POST,
            requestPath = "/api/v1/services/aigc/text-generation/generation",
            requestParameterMap = emptyMap(),
            requestHeaders = coded.requestHeaders.also {
                if (chatRequestBody.stream) {
                    it.set("X-DashScope-SSE", "enable")
                }
            },
            requestBody = requestBody,
            sourceCodedRequestBundle = coded,
        )
    }
}

class DashScopeResponseCodec : ResponseCodec() {
    override fun encode(raw: ResponseBundle): CodedResponseBundle {
        return when (raw.requestBundle.requestPath) {
            "/api/v1/services/aigc/text-generation/generation" -> {
                encodeChat(raw)
            }

            "/api/v1/services/embeddings/text-embedding/text-embedding" -> {
                encodeEmbeddings(raw)
            }

            else -> {
                CodedResponseBundle(
                    scene = Scene.OTHER,
                    responseStatus = raw.responseStatus,
                    responseHeaders = raw.responseHeaders,
                    responseBody = raw.responseBody,
                    rawResponseBundle = raw,
                )
            }
        }
    }

    private fun encodeEmbeddings(raw: ResponseBundle): CodedResponseBundle {
        TODO()
    }

    private fun encodeChat(raw: ResponseBundle): CodedResponseBundle {
        val dashScopeChatResponseBody = JsonUtils.parseJsonString(
            raw.responseBody,
            DashScopeChatResponseBody::class.java
        )
        val dashScopeChatRequestBody = JsonUtils.parseJsonString(
            raw.requestBundle.requestBody,
            DashScopeChatRequestBody::class.java
        )

        val rawChoices = dashScopeChatResponseBody.output.choices ?: listOf(
            DashScopeChoice(
                DashScopeMessage(MessageRole.ASSISTANT.code, dashScopeChatResponseBody.output.text!!),
                dashScopeChatResponseBody.output.finishReason!!
            )
        )

        val choices = rawChoices.mapIndexed { index, dashScopeChoose ->
            Choice(
                index,
                Message(dashScopeChoose.message.role, dashScopeChoose.message.content),
                dashScopeChoose.finishReason
            )
        }

        val chatResponseBody = ChatResponseBody(
            dashScopeChatResponseBody.requestId,
            choices,
            Instant.now().epochSecond.toInt(),
            dashScopeChatRequestBody.model,
            UUID.randomUUID().toString(),
            "chat.completion",
            Usage(
                dashScopeChatResponseBody.usage.outputTokens,
                dashScopeChatResponseBody.usage.inputTokens,
                dashScopeChatResponseBody.usage.totalTokens
            ),
        )
        val responseBody = JsonUtils.toJsonString(chatResponseBody)

        return CodedResponseBundle(
            scene = Scene.CHAT,
            responseStatus = raw.responseStatus,
            responseHeaders = raw.responseHeaders,
            responseBody = responseBody,
            rawResponseBundle = raw,
        )
    }

    override fun decode(coded: CodedResponseBundle): ResponseBundle {
        return when (coded.scene) {
            Scene.CHAT -> {
                decodeChat(coded)
            }

            Scene.EMBEDDINGS -> {
                decodeEmbeddings(coded)
            }

            else -> {
                ResponseBundle(
                    responseStatus = coded.responseStatus,
                    responseHeaders = coded.responseHeaders,
                    responseBody = coded.responseBody,
                    requestBundle = coded.rawResponseBundle.requestBundle.sourceCodedRequestBundle!!.rawRequestBundle,
                )
            }
        }
    }

    private fun decodeEmbeddings(coded: CodedResponseBundle): ResponseBundle {
        TODO()
    }

    private fun decodeChat(coded: CodedResponseBundle): ResponseBundle {
        val chatResponseBody = JsonUtils.parseJsonString(coded.responseBody, ChatResponseBody::class.java)
        val choices = chatResponseBody.choices.map {
            DashScopeChoice(
                DashScopeMessage(it.message.role, it.message.content),
                it.finishReason
            )
        }

        val dashScopeChatResponseBody = DashScopeChatResponseBody(
            chatResponseBody.id, DashScopeOutput(
                text = null,
                finishReason = null,
                choices,
            ), DashScopeUsage(
                chatResponseBody.usage.completionTokens,
                chatResponseBody.usage.promptTokens,
                chatResponseBody.usage.totalTokens
            )
        )
        val responseBody = JsonUtils.toJsonString(dashScopeChatResponseBody)

        return ResponseBundle(
            responseStatus = coded.responseStatus,
            responseHeaders = coded.responseHeaders,
            responseBody = responseBody,
            requestBundle = coded.rawResponseBundle.requestBundle.sourceCodedRequestBundle!!.rawRequestBundle,
        )
    }
}
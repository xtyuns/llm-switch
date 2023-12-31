package io.github.xtyuns.llmhub.llm.codec

import io.github.xtyuns.llmhub.core.*

class DashScopeRequestCodec : RequestCodec() {
    override fun encode(raw: RequestBundle): CodedRequestBundle {
        return when (raw.requestPath) {
            "/v1/chat/completions" -> {
                encodeChat(raw)
            }

            "/v1/embeddings" -> {
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
        val requestBody = raw.requestBody
        val requestBodyMap = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        val requestBodyMap2 = requestBodyMap.toMutableMap()
        requestBodyMap2["text"] = requestBodyMap2["text"]!!.replace("\n", " ")
        val requestBody2 = requestBodyMap2.map { "${it.key}=${it.value}" }.joinToString("&")
        return CodedRequestBundle(
            scene = Scene.EMBEDDINGS,
            requestMethod = raw.requestMethod,
            requestPath = raw.requestPath,
            requestParameterMap = raw.requestParameterMap,
            requestHeaders = raw.requestHeaders,
            requestBody = requestBody2,
            rawRequestBundle = raw,
        )
    }

    private fun encodeChat(raw: RequestBundle): CodedRequestBundle {
        val requestBody = raw.requestBody
        val requestBodyMap = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        val prompt = requestBodyMap["prompt"]!!
        val requestBodyMap2 = requestBodyMap.toMutableMap()
        requestBodyMap2["prompt"] = prompt.replace("\n", " ")
        val requestBody2 = requestBodyMap2.map { "${it.key}=${it.value}" }.joinToString("&")
        return CodedRequestBundle(
            scene = Scene.CHAT,
            requestMethod = raw.requestMethod,
            requestPath = raw.requestPath,
            requestParameterMap = raw.requestParameterMap,
            requestHeaders = raw.requestHeaders,
            requestBody = requestBody2,
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
                coded.rawRequestBundle
            }
        }
    }

    private fun decodeEmbeddings(coded: CodedRequestBundle): RequestBundle {
        val raw = coded.rawRequestBundle
        val requestBody = raw.requestBody
        val requestBodyMap = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        val requestBodyMap2 = requestBodyMap.toMutableMap()
        requestBodyMap2["text"] = requestBodyMap2["text"]!!.replace(" ", "\n")
        val requestBody2 = requestBodyMap2.map { "${it.key}=${it.value}" }.joinToString("&")
        return RequestBundle(
            requestMethod = raw.requestMethod,
            requestPath = raw.requestPath,
            requestParameterMap = raw.requestParameterMap,
            requestHeaders = raw.requestHeaders,
            requestBody = requestBody2,
        )
    }

    private fun decodeChat(coded: CodedRequestBundle): RequestBundle {
        val raw = coded.rawRequestBundle
        val requestBody = raw.requestBody
        val requestBodyMap = requestBody.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        val prompt = requestBodyMap["prompt"]!!
        val requestBodyMap2 = requestBodyMap.toMutableMap()
        requestBodyMap2["prompt"] = prompt.replace(" ", "\n")
        val requestBody2 = requestBodyMap2.map { "${it.key}=${it.value}" }.joinToString("&")
        return RequestBundle(
            requestMethod = raw.requestMethod,
            requestPath = raw.requestPath,
            requestParameterMap = raw.requestParameterMap,
            requestHeaders = raw.requestHeaders,
            requestBody = requestBody2,
        )
    }
}

class DashScopeResponseCodec : ResponseCodec() {
    override fun encode(raw: ResponseBundle): CodedResponseBundle {
        return when (raw.requestBundle.requestPath) {
            "/v1/chat/completions" -> {
                encodeChat(raw)
            }

            "/v1/embeddings" -> {
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
        val responseBody = raw.responseBody
        val responseBodyMap = responseBody.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        val choices = responseBodyMap["choices"]!!
        val responseBodyMap2 = responseBodyMap.toMutableMap()
        responseBodyMap2["choices"] = choices.replace(" ", "\n")
        val responseBody2 = responseBodyMap2.map { "${it.key}=${it.value}" }.joinToString("&")
        return CodedResponseBundle(
            scene = Scene.EMBEDDINGS,
            responseStatus = raw.responseStatus,
            responseHeaders = raw.responseHeaders,
            responseBody = responseBody2,
            rawResponseBundle = raw,
        )
    }

    private fun encodeChat(raw: ResponseBundle): CodedResponseBundle {
        val responseBody = raw.responseBody
        val responseBodyMap = responseBody.split("&").associate {
            val (key, value) = it.split("=")
            key to value
        }
        val choices = responseBodyMap["choices"]!!
        val responseBodyMap2 = responseBodyMap.toMutableMap()
        responseBodyMap2["choices"] = choices.replace("\n", " ")
        val responseBody2 = responseBodyMap2.map { "${it.key}=${it.value}" }.joinToString("&")
        return CodedResponseBundle(
            scene = Scene.CHAT,
            responseStatus = raw.responseStatus,
            responseHeaders = raw.responseHeaders,
            responseBody = responseBody2,
            rawResponseBundle = raw,
        )
    }

    override fun decode(coded: CodedResponseBundle): ResponseBundle {
        TODO("Not yet implemented")
    }
}
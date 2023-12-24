package io.github.xtyuns.llmhub.core

abstract class Codec {
    abstract fun encode(data: String, apiPath: String): String
    abstract fun decode(data: String, apiPath: String): String
}

abstract class RequestCodec : Codec()
abstract class ResponseCodec : Codec()
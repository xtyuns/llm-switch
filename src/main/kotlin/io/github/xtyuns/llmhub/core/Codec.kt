package io.github.xtyuns.llmhub.core

abstract class Codec<R, C> {
    abstract fun encode(raw: R): C
    abstract fun decode(coded: C): R
}

abstract class RequestCodec : Codec<RequestBundle, CodedRequestBundle>() {
    abstract override fun encode(raw: RequestBundle): CodedRequestBundle

    abstract override fun decode(coded: CodedRequestBundle): RequestBundle
}

abstract class ResponseCodec : Codec<ResponseBundle, CodedResponseBundle>() {
    abstract override fun encode(raw: ResponseBundle): CodedResponseBundle

    abstract override fun decode(coded: CodedResponseBundle): ResponseBundle
}
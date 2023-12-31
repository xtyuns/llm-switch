package io.github.xtyuns.llmhub.core

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class ResponseBundle(
    val responseStatus: HttpStatus,
    val responseHeaders: HttpHeaders,
    val responseBody: String,
    val requestBundle: RequestBundle,
)

class CodedResponseBundle(
    val scene: Scene,
    val responseStatus: HttpStatus,
    val responseHeaders: HttpHeaders,
    val responseBody: String,
    val rawResponseBundle: ResponseBundle,
)
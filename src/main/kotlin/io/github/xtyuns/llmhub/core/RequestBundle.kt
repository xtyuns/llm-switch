package io.github.xtyuns.llmhub.core

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

open class RequestBundle(
    val requestMethod: HttpMethod,
    val requestPath: String,
    val requestParameterMap: Map<String, Array<String>>,
    val requestHeaders: HttpHeaders,
    val requestBody: String,
)

class CodedRequestBundle(
    val scene: Scene,
    val requestMethod: HttpMethod,
    val requestPath: String,
    val requestParameterMap: Map<String, Array<String>>,
    val requestHeaders: HttpHeaders,
    val requestBody: String,
    val rawRequestBundle: RequestBundle,
)
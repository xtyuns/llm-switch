package io.github.xtyuns.llmhub.controller

import io.github.xtyuns.llmhub.core.RequestBundle
import io.github.xtyuns.llmhub.llm.LlmService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/llm")
class LlmController(
    val llmService: LlmService
) {
    @RequestMapping("/{channelTag}/{brandName}/**")
    fun invoke(
        @PathVariable channelTag: String,
        @PathVariable brandName: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val uri = request.requestURI
        val prefix = "/${channelTag}/${brandName}/"
        val indexOf = "${uri}/".indexOf(prefix)
        val apiPath = uri.substring(indexOf + prefix.length - 1)

        val requestHeaders = HttpHeaders()
        for (headerName in request.headerNames) {
            requestHeaders[headerName] = request.getHeaders(headerName).toList()
        }

        val rawRequestBundle = RequestBundle(
            HttpMethod.valueOf(request.method),
            apiPath,
            request.parameterMap,
            requestHeaders,
            request.reader.readText()
        )

        val responseBundle = llmService.process(channelTag, brandName, rawRequestBundle)

        response.status = responseBundle.responseStatus.value()
        for (item in responseBundle.responseHeaders.entries) {
            item.value?.forEach {
                response.addHeader(item.key, it)
            }
        }
        response.writer.write(responseBundle.responseBody)
        response.flushBuffer()
    }
}
package io.github.xtyuns.llmhub.controller

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
    @RequestMapping("/{channelName}/{brandName}/**")
    fun invoke(
        @PathVariable channelName: String,
        @PathVariable brandName: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        val uri = request.requestURI
        val prefix = "/${channelName}/${brandName}/"
        val indexOf = "${uri}/".indexOf(prefix)
        val apiPath = uri.substring(indexOf + prefix.length - 1)

        val requestHeaders = HttpHeaders()
        for (headerName in request.headerNames) {
            requestHeaders[headerName] = request.getHeaders(headerName).toList()
        }

        val (httpStatus, responseHeaders, responseText) = llmService.process(
            channelName,
            brandName,
            HttpMethod.valueOf(request.method),
            apiPath,
            requestHeaders,
            request.reader.readText()
        )

        response.status = httpStatus.value()
        for (headerName in responseHeaders.keys) {
            responseHeaders[headerName]?.forEach {
                response.addHeader(headerName, it)
            }
        }
        response.writer.write(responseText)
        response.flushBuffer()
    }
}
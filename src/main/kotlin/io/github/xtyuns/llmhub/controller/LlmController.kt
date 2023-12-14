package io.github.xtyuns.llmhub.controller

import io.github.xtyuns.llmhub.llm.LlmService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/llm")
class LlmController(
    val llmService: LlmService
) {
    @RequestMapping("/{apiStyle}/**")
    fun hello(@PathVariable apiStyle: String, request: HttpServletRequest, response: HttpServletResponse) {
        val uri = request.requestURI
        val indexOf = "${uri}/".indexOf("/${apiStyle}/")
        val apiPath = uri.substring(indexOf + apiStyle.length + 1)
        val requestHeaders = HttpHeaders()
        for (headerName in request.headerNames) {
            requestHeaders[headerName] = request.getHeaders(headerName).toList()
        }

        val (httpStatus, responseHeaders, responseText) = llmService.process(
            apiStyle,
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
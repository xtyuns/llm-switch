package io.github.xtyuns.llmhub.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import java.util.zip.GZIPInputStream

interface Brand {
    private val logger: Logger
        get() = LoggerFactory.getLogger(Brand::class.java)

    val name: String;
    val requestCodec: RequestCodec;
    val responseCodec: ResponseCodec;
    val baseUrl: String;

    fun invoke(
        method: HttpMethod,
        path: String,
        headers: HttpHeaders,
        data: String
    ): Triple<HttpStatus, HttpHeaders, String> {
        val url = "${baseUrl}${path}"
        return request(url, method, headers, data)
    }

    fun request(
        url: String,
        method: HttpMethod,
        headers: HttpHeaders,
        data: String
    ): Triple<HttpStatus, HttpHeaders, String> {
        val restTemplate = RestTemplate()
        val executed = restTemplate.execute(url, method, { request ->
            headers.remove(HttpHeaders.CONTENT_LENGTH)
            request.headers.addAll(headers)
            val bys = data.toByteArray(Charsets.UTF_8)
            request.body.write(bys)
        }, { response ->
            val responseHeaders = HttpHeaders()
            response.headers.forEach { (key, value) ->
                responseHeaders[key] = value
            }
            responseHeaders.remove(HttpHeaders.CONTENT_LENGTH)
            val gzipInputStream = GZIPInputStream(response.body)
            val responseData = String(gzipInputStream.readAllBytes(), Charsets.UTF_8)
            logger.info("response: {}", responseData)
            Triple(HttpStatus.valueOf(response.statusCode.value()), responseHeaders, responseData)
        })
        return executed!!
    }
}
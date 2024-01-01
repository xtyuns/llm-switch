package io.github.xtyuns.llmswitch.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder
import java.util.zip.GZIPInputStream

interface Brand {
    val name: String
    val defaultBaseUrl: String
    val requestCodec: RequestCodec
    val responseCodec: ResponseCodec
    val logger: Logger
        get() = LoggerFactory.getLogger(javaClass)

    fun invoke(requestBundle: RequestBundle, baseUrl: String? = null): ResponseBundle {
        val query = requestBundle.requestParameterMap.entries.joinToString(prefix = "?", separator = "&") {
            "${it.key}=${URLEncoder.encode(it.value.contentToString(), Charsets.UTF_8)}"
        }
        val url = "${baseUrl ?: defaultBaseUrl}${requestBundle.requestPath}${query}"

        val (httpStatus, httpHeaders, data) = request(
            requestBundle.requestMethod,
            url,
            requestBundle.requestHeaders,
            requestBundle.requestBody
        )

        return ResponseBundle(HttpStatus.valueOf(httpStatus.value()), httpHeaders, data, requestBundle)
    }

    fun request(
        method: HttpMethod,
        url: String,
        headers: HttpHeaders,
        data: String
    ): Triple<HttpStatusCode, HttpHeaders, String> {
        val restTemplate = RestTemplate()
        try {
            val executed = restTemplate.execute(url, method, { request ->
                listOf(
                    HttpHeaders.ACCEPT,
                    HttpHeaders.ACCEPT_ENCODING,
                    HttpHeaders.AUTHORIZATION,
                    HttpHeaders.CONTENT_TYPE,
                    HttpHeaders.USER_AGENT,
                ).forEach {
                    request.headers[it] = headers[it]
                }
                val bys = data.toByteArray(Charsets.UTF_8)
                request.body.write(bys)
            }, { response ->
                val responseHeaders = HttpHeaders()
                listOf(
                    HttpHeaders.CONTENT_TYPE,
                ).forEach {
                    responseHeaders[it] = response.headers[it]
                }
                responseHeaders.contentType?.let {
                    if (it.charset == null) {
                        responseHeaders.contentType = MediaType.parseMediaType("$it;charset=utf-8")
                    }
                }
                val responseInputStream =
                    if (response.headers[HttpHeaders.CONTENT_ENCODING]?.contains("gzip") == true) {
                        GZIPInputStream(response.body)
                    } else {
                        response.body
                    }
                val responseData = String(
                    responseInputStream.readAllBytes(),
                    response.headers.contentType?.charset ?: Charsets.UTF_8
                )
                Triple(response.statusCode, responseHeaders, responseData)
            })
            return executed!!
        } catch (e: RestClientResponseException) {
            logger.error("RestClientResponseException: ", e)
            return Triple(e.statusCode, e.responseHeaders ?: HttpHeaders.EMPTY, e.responseBodyAsString)
        } catch (e: Exception) {
            logger.error("Exception: ", e)
            return Triple(HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, "")
        }
    }
}
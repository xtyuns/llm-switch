package io.github.xtyuns.llmhub.core

import org.springframework.http.*
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder

interface Brand {
    val name: String;
    val defaultBaseUrl: String;
    val requestCodec: RequestCodec;
    val responseCodec: ResponseCodec;

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
                listOf(HttpHeaders.CONTENT_TYPE).forEach {
                    responseHeaders[it] = response.headers[it]
                }
                responseHeaders.contentType?.let {
                    if (it.charset == null) {
                        responseHeaders.contentType = MediaType.parseMediaType("$it;charset=utf-8")
                    }
                }
                val responseInputStream = response.body
                val responseData = String(
                    responseInputStream.readAllBytes(),
                    response.headers.contentType?.charset ?: Charsets.UTF_8
                )
                Triple(response.statusCode, responseHeaders, responseData)
            })
            return executed!!
        } catch (e: RestClientResponseException) {
            e.printStackTrace()
            return Triple(e.statusCode, e.responseHeaders ?: HttpHeaders.EMPTY, e.responseBodyAsString)
        } catch (e: Exception) {
            e.printStackTrace()
            return Triple(HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, "")
        }
    }
}
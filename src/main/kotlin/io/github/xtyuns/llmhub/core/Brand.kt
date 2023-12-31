package io.github.xtyuns.llmhub.core

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate
import java.util.zip.GZIPInputStream

interface Brand {
    val name: String;
    val defaultBaseUrl: String;
    val requestCodec: RequestCodec;
    val responseCodec: ResponseCodec;

    fun invoke(requestBundle: RequestBundle, baseUrl: String? = null): ResponseBundle {
        val url = "${baseUrl ?: defaultBaseUrl}${requestBundle.requestPath}"
        val (httpStatus, httpHeaders, data) = request(
            requestBundle.requestMethod,
            url,
            requestBundle.requestParameterMap,
            requestBundle.requestHeaders,
            requestBundle.requestBody
        )

        return ResponseBundle(HttpStatus.valueOf(httpStatus.value()), httpHeaders, data, requestBundle)
    }

    fun request(
        method: HttpMethod,
        url: String,
        parameterMap: Map<String, Array<String>>,
        headers: HttpHeaders,
        data: String
    ): Triple<HttpStatusCode, HttpHeaders, String> {
        val restTemplate = RestTemplate()
        try {
            val executed = restTemplate.execute(url, method, { request ->
                request.headers.addAll(headers)
                request.headers.remove(HttpHeaders.CONTENT_LENGTH)
                request.headers.remove(HttpHeaders.HOST)
                request.headers.remove(HttpHeaders.COOKIE)
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
package io.github.xtyuns.llmhub.llm

import io.github.xtyuns.llmhub.core.Brand
import io.github.xtyuns.llmhub.core.Channel
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class LlmService(
    private val brands: List<Brand>
) {
    fun process(
        channelTag: String,
        brandName: String,
        requestMethod: HttpMethod,
        requestPath: String,
        requestHeaders: HttpHeaders,
        requestData: String,
    ): Triple<HttpStatus, HttpHeaders, String> {
        val brand =
            getBrand(brandName) ?: return Triple(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, "brand not found")
        val channel =
            getChannel(channelTag) ?: return Triple(HttpStatus.BAD_REQUEST, HttpHeaders.EMPTY, "channel not found")

        val channelRequestData = if (brand.name == channel.brand.name) {
            requestData
        } else {
            val encodeRequestData = brand.requestCodec.encode(requestData, requestPath)
            channel.brand.requestCodec.decode(encodeRequestData, requestPath)
        }

        val (responseStatus, responseHeaders, channelResponseData) = channel.brand.invoke(
            requestMethod,
            requestPath,
            requestHeaders,
            channelRequestData
        )

        val responseData = if (brand.name == channel.brand.name) {
            channelResponseData
        } else {
            val encodeResponseData = channel.brand.responseCodec.encode(channelResponseData, requestPath)
            brand.responseCodec.decode(encodeResponseData, requestPath)
        }

        return Triple(responseStatus, responseHeaders, responseData)
    }

    private fun getChannel(channelTag: String): Channel? {
        val brand = brands.firstOrNull() ?: return null
        return Channel(channelTag, brand, 0)
    }

    private fun getBrand(apiStyle: String): Brand? {
        return brands.firstOrNull { it.name == apiStyle }
    }
}
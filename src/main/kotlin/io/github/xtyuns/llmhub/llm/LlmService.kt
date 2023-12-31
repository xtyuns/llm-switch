package io.github.xtyuns.llmhub.llm

import io.github.xtyuns.llmhub.core.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class LlmService(
    private val brands: List<Brand>
) {
    fun process(
        channelTag: String,
        brandName: String,
        rawRequestBundle: RequestBundle,
    ): ResponseBundle {
        val brand = getBrand(brandName) ?: return ResponseBundle(
            HttpStatus.BAD_REQUEST,
            HttpHeaders.EMPTY,
            "brand not found",
            rawRequestBundle
        )
        val channel = getChannel(channelTag) ?: return ResponseBundle(
            HttpStatus.BAD_REQUEST,
            HttpHeaders.EMPTY,
            "channel not found",
            rawRequestBundle
        )

        val codedRequestBundle = if (brand.name == channel.brand.name) {
            CodedRequestBundle(
                Scene.OTHER,
                rawRequestBundle.requestMethod,
                rawRequestBundle.requestPath,
                rawRequestBundle.requestParameterMap,
                rawRequestBundle.requestHeaders,
                rawRequestBundle.requestBody,
                rawRequestBundle,
            )
        } else {
            brand.requestCodec.encode(rawRequestBundle)
        }
        val requestBundle = channel.brand.requestCodec.decode(codedRequestBundle)

        val rawResponseBundle = channel.invoke(requestBundle)

        val codedResponseBundle = if (brand.name == channel.brand.name) {
            CodedResponseBundle(
                Scene.OTHER,
                rawResponseBundle.responseStatus,
                rawResponseBundle.responseHeaders,
                rawResponseBundle.responseBody,
                rawResponseBundle,
            )
        } else {
            channel.brand.responseCodec.encode(rawResponseBundle)
        }
        val responseBundle = brand.responseCodec.decode(codedResponseBundle)

        return responseBundle
    }

    private fun getChannel(channelTag: String): Channel? {
        val brand = brands.firstOrNull {
            it.name == channelTag
        } ?: return null
        return Channel(channelTag, brand).also {
            it.baseUrl = "http://159.75.85.166:16002"
        }
    }

    private fun getBrand(apiStyle: String): Brand? {
        return brands.firstOrNull { it.name == apiStyle }
    }
}
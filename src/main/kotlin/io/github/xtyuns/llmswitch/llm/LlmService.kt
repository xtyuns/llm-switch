package io.github.xtyuns.llmswitch.llm

import io.github.xtyuns.llmswitch.dao.ChannelEntityRepository
import io.github.xtyuns.llmswitch.core.Brand
import io.github.xtyuns.llmswitch.core.Channel
import io.github.xtyuns.llmswitch.core.CodedRequestBundle
import io.github.xtyuns.llmswitch.core.CodedResponseBundle
import io.github.xtyuns.llmswitch.core.RequestBundle
import io.github.xtyuns.llmswitch.core.ResponseBundle
import io.github.xtyuns.llmswitch.core.Scene
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class LlmService(
    private val brands: List<Brand>,
    private val channelEntityRepository: ChannelEntityRepository,
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
        val channel = pickChannel(channelTag) ?: return ResponseBundle(
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

    private fun pickChannel(channelTag: String): Channel? {
        val channelEntity = channelEntityRepository.findFirstByTagWithDescPriority(channelTag) ?: return null
        val brand = getBrand(channelEntity.brandName!!) ?: return null
        return Channel(channelEntity.name!!, brand).also {
            channelEntity.tags?.let { tags ->
                it.tags.addAll(tags)
            }
            it.priority = channelEntity.priority!!
            it.baseUrl = channelEntity.baseUrl
        }
    }

    private fun getBrand(brandName: String): Brand? {
        return brands.firstOrNull { it.name == brandName }
    }
}
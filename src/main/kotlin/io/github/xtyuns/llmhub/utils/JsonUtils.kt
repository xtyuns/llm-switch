package io.github.xtyuns.llmhub.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    private val objectMapper = ObjectMapper()

    fun toJsonString(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    fun <T> parseJsonString(data: String, type: TypeReference<T>): T {
        return objectMapper.readValue(data, type)
    }
}
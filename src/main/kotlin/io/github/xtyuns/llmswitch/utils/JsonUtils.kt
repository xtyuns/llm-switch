package io.github.xtyuns.llmswitch.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object JsonUtils {
    private val objectMapper = ObjectMapper().also {
        it.registerModules(KotlinModule.Builder().build())
    }

    fun toJsonString(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    fun <T> parseJsonString(data: String, type: TypeReference<T>): T {
        return objectMapper.readValue(data, type)
    }

    fun <T> parseJsonString(data: String, valueType: Class<T>): T {
        return objectMapper.readValue(data, valueType)
    }
}
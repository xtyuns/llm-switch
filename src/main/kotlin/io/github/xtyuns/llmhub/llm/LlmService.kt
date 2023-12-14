package io.github.xtyuns.llmhub.llm

import io.github.xtyuns.llmhub.utils.JsonUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils

@Service
class LlmService {
    fun process(
        apiStyle: String,
        apiPath: String,
        headers: HttpHeaders,
        data: String
    ): Triple<HttpStatus, HttpHeaders, String> {
        return Triple(
            HttpStatus.OK,
            HttpHeaders().also {
                it[HttpHeaders.CONTENT_TYPE] = MimeTypeUtils.APPLICATION_JSON_VALUE
            },
            "{" +
                    "\"apiStyle\":\"${apiStyle}\"," +
                    "\"apiPath\":\"${apiPath}\"," +
                    "\"headers\":${JsonUtils.toJsonString(headers)}," +
                    "\"data\":\"${data}\"" +
                    "}"
        )
    }
}
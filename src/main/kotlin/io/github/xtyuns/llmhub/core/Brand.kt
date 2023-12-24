package io.github.xtyuns.llmhub.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI

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
        val connection = URI("${baseUrl}${path}").toURL().openConnection() as HttpURLConnection
        connection.requestMethod = method.name()
        headers.forEach { (k, v) -> connection.setRequestProperty(k, v.joinToString(";")) }
        connection.doOutput = true
        connection.outputStream.write(data.toByteArray())

        val responseCode = connection.responseCode
        val responseHeaders = HttpHeaders().also {
            connection.headerFields.filter { (k, _) -> k != null }.forEach { (k, v) -> it[k] = v }
        }
        val sbd = StringBuilder()
        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sbd.append(line)
            }
        } catch (e: Exception) {
            logger.error("read response error", e)
        }

        return Triple(HttpStatus.valueOf(responseCode), responseHeaders, sbd.toString())
    }
}
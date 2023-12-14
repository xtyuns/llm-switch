package io.github.xtyuns.llmhub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LlmHubApplication

fun main(args: Array<String>) {
    runApplication<LlmHubApplication>(*args)
}

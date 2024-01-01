package io.github.xtyuns.llmswitch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LlmSwitchApplication

fun main(args: Array<String>) {
    runApplication<LlmSwitchApplication>(*args)
}

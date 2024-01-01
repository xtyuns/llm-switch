package io.github.xtyuns.llmhub.core

class Channel(val name: String, val brand: Brand) {
    val tags: MutableSet<String> = mutableSetOf()
    var priority: Int = 0
    var baseUrl: String? = null

    fun invoke(requestBundle: RequestBundle): ResponseBundle {
        return brand.invoke(requestBundle, baseUrl)
    }
}
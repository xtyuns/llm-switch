package io.github.xtyuns.llmhub.dao

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
class ChannelEntity {
    @Id
    var id: Long? = null

    var name: String? = null

    var brandName: String? = null

    var priority: Int? = null
}
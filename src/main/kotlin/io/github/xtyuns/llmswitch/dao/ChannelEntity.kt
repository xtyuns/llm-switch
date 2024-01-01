package io.github.xtyuns.llmswitch.dao

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "channel")
open class ChannelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "name", nullable = false, length = 32)
    open var name: String? = null

    @Column(name = "brand_name", nullable = false, length = 32)
    open var brandName: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags")
    open var tags: MutableSet<String>? = null

    @Column(name = "priority", nullable = false)
    open var priority: Int? = null

    @Column(name = "base_url")
    open var baseUrl: String? = null
}
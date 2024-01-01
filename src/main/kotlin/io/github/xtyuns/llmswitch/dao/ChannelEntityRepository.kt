package io.github.xtyuns.llmswitch.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface ChannelEntityRepository : JpaRepository<ChannelEntity, Long>, JpaSpecificationExecutor<ChannelEntity> {
    @Query("SELECT * FROM channel c WHERE :value MEMBER OF(c.tags) ORDER BY c.priority DESC", nativeQuery = true)
    fun findFirstByTagWithDescPriority(value: String): ChannelEntity?
}
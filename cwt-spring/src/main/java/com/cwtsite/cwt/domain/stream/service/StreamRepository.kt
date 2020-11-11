package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.stream.entity.Stream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface StreamRepository : JpaRepository<Stream, String> {

    @Query("""
        select s from Stream s
        where s.id not in (select g.stream.id from Game g where g.stream is not null)
    """)
    fun findWithoutGame(): List<Stream>
}

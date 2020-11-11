package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface StreamRepository : JpaRepository<Stream, String> {

    fun findByGameIsNull(): List<Stream>

    @Query("""
       select distinct lower(g.homeUser.username) from Game g
       where g.group is not null and g.tournament = :tournament
        and g.id not in (select s.game.id from Stream s)
    """)
    fun findDistinctHomeUsernamesToLowercaseInGroup(@Param("tournament") tournament: Tournament): List<String>

    @Query("""
       select distinct lower(g.awayUser.username) from Game g
       where g.group is not null and g.tournament = :tournament
        and g.id not in (select s.game.id from Stream s)
    """)
    fun findDistinctAwayUsernamesToLowercaseInGroup(@Param("tournament") tournament: Tournament): List<String>

    @Query("""
       select distinct lower(g.homeUser.username) from Game g
       where g.playoff is not null and g.tournament = :tournament
        and g.id not in (select s.game.id from Stream s)
    """)
    fun findDistinctHomeUsernamesToLowercaseInPlayoffs(@Param("tournament") tournament: Tournament): List<String>

    @Query("""
       select distinct lower(g.awayUser.username) from Game g
       where g.playoff is not null and g.tournament = :tournament
        and g.id not in (select s.game.id from Stream s)
    """)
    fun findDistinctAwayUsernamesToLowercaseInPlayoffs(@Param("tournament") tournament: Tournament): List<String>

    @Query("""
       select distinct lower(g.homeUser.username) from Game g
        where g.id not in (select s.game.id from Stream s)
    """)
    fun findDistinctHomeUsernamesToLowercase(): List<String>

    @Query("""
       select distinct lower(g.awayUser.username) from Game g
        where g.id not in (select s.game.id from Stream s)
    """)
    fun findDistinctAwayUsernamesToLowercase(): List<String>
}

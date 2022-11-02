package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StreamRepository : JpaRepository<Stream, String> {

    fun findByGameIsNull(): List<Stream>

    @Query(
        """
       select distinct lower(g.homeUser.username) from Game g
        where g.tournament = :tournament
         and g.id not in (select s.game.id from Stream s where s.game is not null)
    """
    )
    fun findHomeUsernamesForUnlinkedGames(@Param("tournament") tournament: Tournament): List<String>

    @Query(
        """
       select distinct lower(g.awayUser.username) from Game g
        where g.tournament = :tournament
         and g.id not in (select s.game.id from Stream s where s.game is not null)
    """
    )
    fun findAwayUsernamesForUnlinkedGames(@Param("tournament") tournament: Tournament): List<String>

    @Query(
        """
       select distinct lower(g.homeUser.username) from Game g
        where g.id not in (select s.game.id from Stream s where s.game is not null)
    """
    )
    fun findHomeUsernamesForUnlinkedGames(): List<String>

    @Query(
        """
       select distinct lower(g.awayUser.username) from Game g
        where g.id not in (select s.game.id from Stream s where s.game is not null)
    """
    )
    fun findAwayUsernamesForUnlinkedGames(): List<String>

    fun findByGame(game: Game): List<Stream>

    fun findByChannel(channel: Channel): List<Stream>
}

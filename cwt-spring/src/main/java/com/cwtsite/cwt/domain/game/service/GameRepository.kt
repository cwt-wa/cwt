package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GameRepository : JpaRepository<Game, Long> {

    fun findByTournamentAndPlayoffIsNotNull(tournament: Tournament): List<Game>

    fun findByTournamentAndPlayoffIsNotNullAndVoidedFalse(tournament: Tournament): List<Game>

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null " +
            "and g.awayUser is not null) and g.reporter is null and g.tournament = :tournament and g.group is null")
    fun findNextPlayoffGameForUser(
            @Param("tournament") tournament: Tournament,
            @Param("user") user: User): Game

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null " +
            "and g.awayUser is not null) and g.reporter is not null and g.group = :group and g.voided = false")
    fun findPlayedByUserInGroup(
            @Param("user") user: User,
            @Param("group") group: Group): List<Game>

    @Query("select g from Game g where g.tournament = :tournament and g.playoff.round = :round and g.playoff.spot = :spot")
    fun findGameInPlayoffTree(
            @Param("tournament") tournament: Tournament,
            @Param("round") round: Int,
            @Param("spot") spot: Int): Optional<Game>

    @Query("select g from Game g where g.tournament = :tournament and g.playoff.round = :round " +
            "and (g.homeUser = :user or g.awayUser = :user)")
    fun findGameInPlayoffTree(
            @Param("tournament") tournament: Tournament,
            @Param("user") user: User,
            @Param("round") round: Int): List<Game>

    @Query(value = "select g from Game g where g.playoff.round >= :finalRound and g.homeUser is not null and g.awayUser is not null " +
            "and g.tournament = :tournament")
    fun findReadyGamesInRoundEqualOrGreaterThan(@Param("finalRound") finalRound: Int, @Param("tournament") tournament: Tournament): List<Game>

    @Query("select g from Game g where g.playoff.round = :round and g.tournament = :tournament and g.voided = false")
    fun findByTournamentAndRoundAndNotVoided(@Param("tournament") tournament: Tournament,
                                             @Param("round") round: Int): List<Game>

    fun findByGroup(group: Group): List<Game>

    fun findByGroupNotNullAndTournament(tournament: Tournament): List<Game>

    fun findByGroupNotNullAndVoidedFalseAndTournament(tournament: Tournament): List<Game>

    fun findByHomeUserNotNullAndAwayUserNotNullAndScoreHomeNotNullAndScoreAwayNotNull(pageable: Pageable): Page<Game>

    @Query("select g from Game g where (g.homeUser = :user1 and g.awayUser = :user2) or (g.homeUser = :user2 and g.awayUser = :user1)")
    fun findGameOfUsers(pageable: Pageable, @Param("user1") user1: User, @Param("user2") user2: User): Page<Game>

    @Query("select g from Game g where g.homeUser = :user or g.awayUser = :user")
    fun findGameOfUser(page: Pageable, @Param("user") user: User): Page<Game>

    @Query("""
        select g from Game g
        where ((g.homeUser = :user1 and g.awayUser = :user2) or (g.homeUser = :user2 and g.awayUser = :user1))
        order by g.created desc
    """)
    fun findGame(@Param("user1") user1: User, @Param("user2") user2: User): List<Game>

    @Query("""
        select g from Game g
        where g.tournament = :tournament and
         ((g.homeUser = :user1 and g.awayUser = :user2) or (g.homeUser = :user2 and g.awayUser = :user1))
        order by g.created desc
    """)
    fun findGame(@Param("user1") user1: User, @Param("user2") user2: User,
                 @Param("tournament") tournament: Tournament): List<Game>

    fun findByTournament(tournament: Tournament): List<Game>
}

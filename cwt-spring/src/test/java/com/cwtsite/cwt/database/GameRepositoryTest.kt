package com.cwtsite.cwt.database

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Comment
import com.cwtsite.cwt.integration.EmbeddedPostgres
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Hibernate
import org.junit.Assert
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import java.sql.Timestamp

@RunWith(SpringRunner::class)
@DataJpaTest
@EmbeddedPostgres
class GameRepositoryTest : AbstractDatabaseTest() {

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Test
    fun findPlayedByUserInGroup() {
        val tournament = em.persist(Tournament(status = TournamentStatus.GROUP))

        val group = em.persist(Group(tournament = tournament))
        val user = em.persist(User(email = "example@cwtsite.com", username = "EpicUser"))

        val dummyUser = persistDummyUser()
        val dummyGroup = em.persist(Group(tournament = tournament))

        val expectedGame1 = em.persist(Game(
                homeUser = user, awayUser = dummyUser,
                reporter = user, tournament = tournament,
                group = group))

        val expectedGame2 = em.persist(Game(
                homeUser = user, awayUser = dummyUser,
                reporter = user, tournament = tournament,
                group = group))

        // Not yet reported.
        em.persist(Game(
                homeUser = dummyUser, awayUser = user,
                tournament = tournament,
                group = group))

        // Other group.
        em.persist(Game(
                homeUser = dummyUser, awayUser = user,
                tournament = tournament,
                group = dummyGroup))

        // Opponent is null.
        em.persist(Game(
                awayUser = user,
                tournament = tournament,
                reporter = dummyUser,
                group = group))

        // User not included.
        em.persist(Game(
                homeUser = dummyUser,
                awayUser = dummyUser,
                reporter = dummyUser,
                tournament = tournament,
                group = group))

        em.flush()

        em.find(Tournament::class.java, tournament.id!!)
        val actualGroup = em.find(Group::class.java, group.id!!)
        val actualUser = em.find(User::class.java, user.id!!)

        assertEquals(
                gameRepository.findAllById(listOf(expectedGame1.id!!, expectedGame2.id!!)),
                gameRepository.findPlayedByUserInGroup(actualUser, actualGroup))
    }

    @Test
    fun findNextPlayoffGameForUser() {
        val user = em.persist(User(email = "someUser@cwtsite.com", username = "someUser"))

        val tournament = em.persist(Tournament(status = TournamentStatus.GROUP))

        createPlayoffGame(tournament, 1, 1)
        createPlayoffGame(tournament, 1, 2)
        createPlayoffGame(tournament, 1, 4)
        createPlayoffGame(tournament, 2, 1)

        val game13 = createPlayoffGame(tournament, 1, 3)
        game13.homeUser = user
        em.persist(game13)

        val game22 = createPlayoffGame(tournament, 2, 2)
        game22.homeUser = user
        game22.reporter = null
        em.persist(game22)

        em.flush()

        Assert.assertEquals(
                em.find(Game::class.java, game22.id!!),
                gameRepository.findNextPlayoffGameForUser(
                        em.find(Tournament::class.java, tournament.id!!),
                        em.find(User::class.java, user.id!!)))
    }

    private fun createPlayoffGame(tournament: Tournament, round: Int, spot: Int): Game {
        val playoffGame = em.persist(PlayoffGame(
                round = round, spot = spot))

        return em.persist(Game(
                tournament = tournament,
                playoff = playoffGame,
                homeUser = persistDummyUser(),
                awayUser = persistDummyUser(),
                reporter = persistDummyUser()
        ))
    }

    @Test
    fun findGameInPlayoffTree() {
        val user = persistDummyUser()

        val tournament = em.persist(Tournament(status = TournamentStatus.PLAYOFFS))

        createPlayoffGame(tournament, 1, 1)
        createPlayoffGame(tournament, 1, 2)
        val gameJustPlayed = createPlayoffGame(tournament, 1, 3)
        createPlayoffGame(tournament, 1, 4)
        createPlayoffGame(tournament, 2, 1)
        val gameToAdvanceTo = createPlayoffGame(tournament, 2, 2)

        gameJustPlayed.homeUser = user
        gameJustPlayed.scoreHome = 3
        gameJustPlayed.scoreAway = 1

        gameToAdvanceTo.homeUser = null

        em.flush()

        Assert.assertEquals(
                gameRepository.findGameInPlayoffTree(
                        em.find(Tournament::class.java, tournament.id!!), 2, 2).get().id,
                gameToAdvanceTo.id!!)
    }

    @Test
    fun findReadyFinals_positive() {
        val tournament = em.persist(Tournament())

        val thirdPlaceGame = em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 4, spot = 1)),
                homeUser = persistDummyUser(),
                awayUser = persistDummyUser(),
                tournament = tournament
        ))

        val finalGame = em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 5, spot = 1)),
                homeUser = persistDummyUser(),
                awayUser = persistDummyUser(),
                tournament = tournament
        ))

        em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 3, spot = 1)),
                homeUser = thirdPlaceGame.homeUser,
                awayUser = finalGame.homeUser,
                tournament = tournament
        ))

        em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 3, spot = 2)),
                homeUser = finalGame.homeUser,
                awayUser = thirdPlaceGame.homeUser,
                tournament = tournament
        ))

        em.flush()

        assertThat(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(4, tournament))
                .containsExactlyInAnyOrder(
                        em.find(Game::class.java, finalGame.id!!),
                        em.find(Game::class.java, thirdPlaceGame.id!!))

        Assert.assertEquals(4, gameRepository.findReadyGamesInRoundEqualOrGreaterThan(1, tournament).size.toLong())
    }

    @Test
    fun findReadyFinals_negative() {
        val tournament = em.persist(Tournament())

        em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 4, spot = 1)),
                tournament = tournament))

        em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 5, spot = 1)),
                tournament = tournament))

        em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 3, spot = 1)),
                tournament = tournament,
                homeUser = persistDummyUser(),
                awayUser = persistDummyUser()))

        em.persist(Game(
                playoff = em.persist(PlayoffGame(round = 3, spot = 2)),
                tournament = tournament,
                homeUser = persistDummyUser(),
                awayUser = persistDummyUser()))

        em.flush()

        assertThat(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(4, tournament)).isEmpty()
        assertThat(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(1, tournament)).hasSize(2)
    }

    @Test
    fun findAll() {
        val tournament = em.persist(Tournament())
        val gameWithTwoComments = em.persist(Game(tournament = tournament))
        val gameWithOneComment = em.persist(Game(tournament = tournament))

        em.persist(Comment(
                body = "Some comment",
                author = persistDummyUser(),
                game = gameWithOneComment))

        em.persist(Comment(
                body = "Some other comment",
                author = persistDummyUser(),
                game = gameWithTwoComments))

        em.persist(Comment(
                body = "Even more other comments",
                author = persistDummyUser(),
                game = gameWithTwoComments))

        val gameWithNoComment = em.persist(Game(tournament = tournament))

        em.flush()

        val actualPagedGames = gameRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "commentsSize")))

        assertThat(actualPagedGames.content.map { it.id })
                .containsExactly(
                        gameWithTwoComments.id!!,
                        gameWithOneComment.id!!,
                        gameWithNoComment.id!!)
    }

    @Test
    @Ignore("Kotlin data class entity appears to eagerly load this.")
    fun lazyPropertyFetching() {
        val tournament = em.persist(Tournament())
        em.persist(Comment(
                author = persistDummyUser(),
                game = em.persist(Game(tournament = tournament))))

        em.flush()

        val game = em.find(Game::class.java, 1L)
        Assert.assertFalse(Hibernate.isPropertyInitialized(game, "commentsSize"))
        game.commentsSize
        Assert.assertTrue(Hibernate.isPropertyInitialized(game, "commentsSize"))
    }

    @Test
    fun findGameOfUsers() {
        val homeUser = persistDummyUser()
        val awayUser = persistDummyUser()
        val tournament = em.persist(Tournament())
        val game = em.persist(Game(
                homeUser = homeUser,
                awayUser = awayUser,
                tournament = tournament,
                reportedAt = Timestamp(1605360542307)
        ))
        val otherUser = persistDummyUser()
        em.persist(Game(
                homeUser = homeUser,
                awayUser = otherUser,
                tournament = tournament,
                reportedAt = Timestamp(1605360542307)
        ))
        em.flush()
        val actual = gameRepository.findGameOfUsers(PageRequest.of(0, 5), awayUser, homeUser)
        assertThat(actual.content).hasSize(1)
        assertThat(actual.content).allSatisfy { assertThat(it).isEqualTo(game)}
        assertThat(actual.size).isEqualTo(5)
        assertThat(actual.totalElements).isEqualTo(1)
    }
}

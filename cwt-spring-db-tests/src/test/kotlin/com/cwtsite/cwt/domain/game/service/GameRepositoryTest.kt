package com.cwtsite.cwt.domain.game.service

import com.btc.redg.generated.GGame
import com.btc.redg.generated.GTournament
import com.btc.redg.generated.RedG
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.assertj.core.api.Assertions
import org.hibernate.Hibernate
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import test.AbstractDbTest
import java.util.*
import kotlin.test.Ignore
import kotlin.test.Test

open class GameRepositoryTest : AbstractDbTest() {

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Test
    fun findPlayedByUserInGroup() {
        val redG = createRedG()

        val gTournament = redG.addTournament()
                .status(TournamentStatus.GROUP.name)

        val gGroup = redG.addGroup(gTournament)
        val gUser = redG.addUser()

        // The game to find (as away user).
        val expectedGame1 = redG.addGame(gTournament)
                .awayUserIdUser(gUser)
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup)

        // The game to find (as home user).
        val expectedGame2 = redG.addGame(gTournament)
                .awayUserIdUser(redG.dummyUser())
                .homeUserIdUser(gUser)
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup)

        // Not yet reported.
        redG.addGame(gTournament)
                .awayUserIdUser(gUser)
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(null)
                .groupIdGroup(gGroup)

        // Other group.
        redG.addGame(gTournament)
                .awayUserIdUser(gUser)
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(redG.dummyGroup())

        // Opponent is null.
        redG.addGame(gTournament)
                .awayUserIdUser(gUser)
                .homeUserIdUser(null)
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup)

        // User not included.
        redG.addGame(gTournament)
                .awayUserIdUser(redG.dummyUser())
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup)

        insertRedGIntoDatabase(redG)

        em.find(Tournament::class.java, gTournament.id())
        val group = em.find(Group::class.java, gGroup.id())

        val user = loadUsersGetOneWorkaround(gUser.id())

        Assert.assertEquals(
                gameRepository.findAllById(Arrays.asList(expectedGame1.id(), expectedGame2.id())),
                gameRepository.findPlayedByUserInGroup(user, group))
    }

    @Test
    fun findNextPlayoffGameForUser() {
        val redG = createRedG()

        val gUser = redG.addUser()

        val gTournament = redG.addTournament()
                .status(TournamentStatus.PLAYOFFS.name)

        createPlayoffGame(redG, gTournament, 1, 1)
        createPlayoffGame(redG, gTournament, 1, 2)
        createPlayoffGame(redG, gTournament, 1, 4)
        createPlayoffGame(redG, gTournament, 2, 1)

        val gGame13 = createPlayoffGame(redG, gTournament, 1, 3)
        gGame13.homeUserIdUser(gUser)

        val gGame22 = createPlayoffGame(redG, gTournament, 2, 2)
        gGame22.homeUserIdUser(gUser)
        gGame22.reporterIdUser(null)

        insertRedGIntoDatabase(redG)

        val tournament = em.find(Tournament::class.java, gTournament.id())
        val user = loadUsersGetOneWorkaround(gUser.id())

        Assert.assertEquals(
                em.find(Game::class.java, gGame22.id()),
                gameRepository.findNextPlayoffGameForUser(tournament, user))
    }

    private fun createPlayoffGame(redG: RedG, tournament: GTournament, round: Int, spot: Int): GGame {
        val gPlayoffGame = redG.addPlayoffGame()
                .round(round)
                .spot(spot)

        return redG.addGame(tournament)
                .playoffIdPlayoffGame(gPlayoffGame)
                .groupIdGroup(null)
                .homeUserIdUser(redG.dummyUser())
                .awayUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
    }

    private fun loadUsersGetOneWorkaround(userId: Long): User {
        // em.find(User.class, gUser.id()) yields null for some reason...
        return loadUsersWorkaround()
                .find { it.id == userId } ?: throw RuntimeException()
    }

    private fun loadUsersWorkaround(): List<User> {
        // em.find(User.class, gUser.id()) yields null for some reason...
        @Suppress("JpaQlInspection")
        return em.entityManager.createQuery("select u from User u", User::class.java).resultList
    }

    @Test
    fun findGameInPlayoffTree() {
        val redG = createRedG()

        val gUser = redG.addUser()

        val gTournament = redG.addTournament()
                .id(1L)
                .status(TournamentStatus.PLAYOFFS.name)

        createPlayoffGame(redG, gTournament, 1, 1)
        createPlayoffGame(redG, gTournament, 1, 2)
        val gameJustPlayed = createPlayoffGame(redG, gTournament, 1, 3)
        createPlayoffGame(redG, gTournament, 1, 4)
        createPlayoffGame(redG, gTournament, 2, 1)
        val gameToAdvanceTo = createPlayoffGame(redG, gTournament, 2, 2)

        gameJustPlayed
                .homeUserIdUser(gUser)
                .scoreHome(3)
                .scoreAway(1)

        gameToAdvanceTo
                .homeUserIdUser(null)
                .id(99L)

        insertRedGIntoDatabase(redG)
        loadUsersGetOneWorkaround(gUser.id())


        Assert.assertEquals(
                gameRepository.findGameInPlayoffTree(em.find(Tournament::class.java, gTournament.id()), 2, 2).get().id,
                gameToAdvanceTo.id())
    }

    @Test
    fun findReadyFinals_positive() {
        val redG = createRedG()
        val gTournament = redG.addTournament()

        val thirdPlaceGame = redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(4).spot(1))
                .homeUserIdUser(redG.dummyUser())
                .awayUserIdUser(redG.dummyUser())

        val finalGame = redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(5).spot(1))
                .homeUserIdUser(redG.dummyUser())
                .awayUserIdUser(redG.dummyUser())

        redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(3).spot(1))
                .homeUserIdUser(thirdPlaceGame.homeUserIdUser())
                .awayUserIdUser(finalGame.homeUserIdUser())

        redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(3).spot(2))
                .homeUserIdUser(finalGame.homeUserIdUser())
                .awayUserIdUser(thirdPlaceGame.homeUserIdUser())

        insertRedGIntoDatabase(redG)

        loadUsersWorkaround()

        Assertions
                .assertThat(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(4))
                .containsExactlyInAnyOrder(em.find(Game::class.java, finalGame.id()), em.find(Game::class.java, thirdPlaceGame.id()))

        Assert.assertEquals(4, gameRepository.findReadyGamesInRoundEqualOrGreaterThan(1).size.toLong())
    }

    @Test
    fun findReadyFinals_negative() {
        val redG = createRedG()
        val gTournament = redG.addTournament()

        redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(4).spot(1))
                .homeUserIdUser(null)
                .awayUserIdUser(null)

        redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(5).spot(1))
                .homeUserIdUser(null)
                .awayUserIdUser(null)

        redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(3).spot(1))
                .homeUserIdUser(redG.dummyUser())
                .awayUserIdUser(redG.dummyUser())

        redG.addGame(gTournament)
                .playoffIdPlayoffGame(redG.addPlayoffGame().round(3).spot(2))
                .homeUserIdUser(redG.dummyUser())
                .awayUserIdUser(redG.dummyUser())

        insertRedGIntoDatabase(redG)

        loadUsersWorkaround()

        Assertions
                .assertThat(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(4))
                .isEmpty()

        Assert.assertEquals(2, gameRepository.findReadyGamesInRoundEqualOrGreaterThan(1).size.toLong())
    }

    @Test
    fun findAll() {
        val redG = createRedG()

        val gTournament = redG.addTournament()
        val dummyUser = redG.dummyUser()
        val gGameWithTwoComments = redG.addGame(gTournament).id(2)

        redG.addComment(dummyUser, redG.addGame(gTournament).id(1))
        redG.addComment(dummyUser, gGameWithTwoComments)
        redG.addComment(dummyUser, gGameWithTwoComments)
        redG.addGame(gTournament).id(3L)

        insertRedGIntoDatabase(redG)

        val actualPagedGames = gameRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "commentsSize")))

        Assertions
                .assertThat(actualPagedGames.content.map { it.id })
                .containsExactly(2L, 1L, 3L)
    }

    @Test
    @Ignore("Doesn't seem to work with entities in Kotlin. :(")
    fun lazyPropertyFetching() {
        val redG = createRedG()
        val gTournament = redG.addTournament()
        redG.addComment(redG.dummyUser(), redG.addGame(gTournament).id(1L))
        insertRedGIntoDatabase(redG)

        val game = em.find(Game::class.java, 1L)

        Assert.assertFalse(Hibernate.isPropertyInitialized(game, "commentsSize"))

        game.commentsSize
        Assert.assertTrue(Hibernate.isPropertyInitialized(game, "commentsSize"))
    }
}

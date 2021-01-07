package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.Optional

@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceTest {

    @InjectMocks
    private lateinit var playoffService: PlayoffService

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var configurationService: ConfigurationService

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var tournamentService: TournamentService

    @Mock
    private lateinit var treeService: TreeService

    @Mock
    private lateinit var configurationRepository: ConfigurationRepository

    @Test
    fun advanceByGame_playoffGameExists() {
        val tournament = EntityDefaults.tournament()
        val game = createGame(1L, EntityDefaults.user(1L), EntityDefaults.user(2L), 3, 0, createPlayoffGame(1, 3), tournament)
        val upcomingAwayUser = EntityDefaults.user(3L)

        stubFinalGameChecksBiased()
        stubNextPlayoffSpotForOneWayFinalTree(game.playoff!!.round + 1, 2)

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(game.tournament, 2, 2))
                .thenReturn(Optional.of(
                        createGame(2L, null, upcomingAwayUser, null, null, createPlayoffGame(2, 2), tournament)))

        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    Assert.assertEquals(2, actualGame.playoff!!.round)
                    Assert.assertEquals(2, actualGame.playoff!!.spot)
                    Assert.assertEquals(game.tournament, actualGame.tournament)
                    Assert.assertEquals(game.homeUser, actualGame.homeUser)
                    Assert.assertEquals(upcomingAwayUser, actualGame.awayUser)
                    Assert.assertEquals(2L, actualGame.id)
                    Assert.assertNull(actualGame.group)

                    actualGame
                }

        playoffService.advanceByGame(game)
    }

    @Test
    fun advanceByGame_playoffGameDoesNotExist() {
        val game = createGame(1L, EntityDefaults.user(1L), EntityDefaults.user(2L), 2, 3, createPlayoffGame(1, 2), EntityDefaults.tournament())

        stubFinalGameChecksBiased()
        stubNextPlayoffSpotForOneWayFinalTree(game.playoff!!.round + 1, 1)

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(game.tournament, 2, 1))
                .thenReturn(Optional.empty())

        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    Assert.assertEquals(2, actualGame.playoff!!.round.toLong())
                    Assert.assertEquals(1, actualGame.playoff!!.spot.toLong())
                    Assert.assertEquals(game.tournament, actualGame.tournament)
                    Assert.assertEquals(game.awayUser, actualGame.awayUser)
                    Assert.assertNull(actualGame.homeUser)
                    Assert.assertNull(actualGame.id)
                    Assert.assertNull(actualGame.group)

                    actualGame
                }

        playoffService.advanceByGame(game)
    }

    @Test
    fun advanceByGame_advanceAsHomeOrAway() {
        val tournament = EntityDefaults.tournament()
        val gameId = 1L
        val homeUser = EntityDefaults.user(gameId)
        val awayUser = EntityDefaults.user(2L)

        stubFinalGameChecksBiased()

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.empty())

        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    Assert.assertEquals((invocation.getArgument<Any>(0) as Game).homeUser, homeUser)
                    Assert.assertNull((invocation.getArgument<Any>(0) as Game).awayUser)
                    invocation.getArgument(0)
                } // winner home, round 1, spot 3
                .thenAnswer { invocation ->
                    Assert.assertNull((invocation.getArgument<Any>(0) as Game).homeUser)
                    Assert.assertEquals((invocation.getArgument<Any>(0) as Game).awayUser, awayUser)
                    invocation.getArgument(0)
                } // winner away, round 2, spot 4
                .thenAnswer { invocation ->
                    Assert.assertEquals((invocation.getArgument<Any>(0) as Game).homeUser, homeUser)
                    Assert.assertNull((invocation.getArgument<Any>(0) as Game).awayUser)
                    invocation.getArgument(0)
                } // winner home, round 3, spot 1
                .thenAnswer { invocation ->
                    Assert.assertNull((invocation.getArgument<Any>(0) as Game).homeUser)
                    Assert.assertEquals((invocation.getArgument<Any>(0) as Game).awayUser, awayUser)
                    invocation.getArgument(0)
                } // winner away, round 1, spot 8
                .thenAnswer { invocation ->
                    Assert.assertEquals((invocation.getArgument<Any>(0) as Game).homeUser, awayUser)
                    Assert.assertNull((invocation.getArgument<Any>(0) as Game).awayUser)
                    invocation.getArgument(0)
                } // winner away, round 2, spot 1

        listOf(
                Pair(createGame(gameId, homeUser, awayUser, 3, 1, createPlayoffGame(1, 3), tournament), 2),
                Pair(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 4), tournament), 2),
                Pair(createGame(gameId, homeUser, awayUser, 3, 1, createPlayoffGame(3, 1), tournament), 1),
                Pair(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(1, 8), tournament), 4),
                Pair(createGame(gameId, homeUser, awayUser, 0, 3, createPlayoffGame(2, 1), tournament), 1)
        ).forEach {
            stubNextPlayoffSpotForOneWayFinalTree(it.first.playoff!!.round + 1, it.second)
            playoffService.advanceByGame(it.first)
        }
    }

    @Test
    fun advanceByGame_isFinalGame() {
        val game = createGame(1L, EntityDefaults.user(1L), EntityDefaults.user(2L), 2, 3, createPlayoffGame(4, 1), EntityDefaults.tournament())

        stubFinalGameChecksBiased()

        Assertions
                .assertThatThrownBy { playoffService.advanceByGame(game) }
                .isExactlyInstanceOf(RuntimeException::class.java)
                .hasMessage("There's no one-way final game although there's already a third place game.")

        Mockito
                .`when`(gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round + 1))
                .thenReturn(listOf(game.copy(id = 2, reporter = null, scoreAway = null, scoreHome = null)))

        Assertions
                .assertThat(playoffService.advanceByGame(game))
                .isEmpty()
    }

    private fun createGame(id: Long?,
                           homeUser: User?, awayUser: User,
                           scoreHome: Int?, scoreAway: Int?,
                           playoffGame: PlayoffGame, tournament: Tournament) = Game(
            id = id,
            homeUser = homeUser,
            awayUser = awayUser,
            scoreHome = scoreHome,
            scoreAway = scoreAway,
            playoff = playoffGame,
            tournament = tournament
    )

    /** Biased by assuming that number of groups is eight and two advancing each. */
    private fun stubFinalGameChecksBiased() {
        Mockito
                .`when`(treeService.isFinalGame(MockitoUtils.anyObject(), Mockito.anyInt()))
                .thenAnswer { it.getArgument<Int>(1) == 5 }

        Mockito
                .`when`(treeService.isThirdPlaceGame(MockitoUtils.anyObject(), Mockito.anyInt()))
                .thenAnswer { it.getArgument<Int>(1) == 4 }
        Mockito
                .`when`(treeService.isThreeWayFinalGame(MockitoUtils.anyObject(), Mockito.anyInt()))
                .thenReturn(false)
    }

    private fun stubNextPlayoffSpotForOneWayFinalTree(nextRound: Int, nextSpot: Int) {
        Mockito
                .`when`(treeService.nextPlayoffSpotForOneWayFinalTree(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Pair(nextRound, nextSpot))
    }

    private fun createPlayoffGame(round: Int, spot: Int) = PlayoffGame(
            round = round,
            spot = spot
    )
}


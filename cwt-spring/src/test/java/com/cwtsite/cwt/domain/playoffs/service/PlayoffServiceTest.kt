package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
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
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

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


    @Test
    fun advanceByGame_playoffGameExists() {
        val tournament = EntityDefaults.tournament()
        val game = createGame(1L, EntityDefaults.user(1L), EntityDefaults.user(2L), 3, 0, createPlayoffGame(1, 3), tournament)
        val upcomingAwayUser = EntityDefaults.user(3L)

        mockCountByTournament(game.tournament)
        mockNumberOfGroupMembersAdvancing()

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

        mockCountByTournament(game.tournament)
        mockNumberOfGroupMembersAdvancing()

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(game.tournament, 2, 1))
                .thenReturn(Optional.empty())

        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    Assert.assertEquals(2, (actualGame.playoff!!.round as Int).toLong())
                    Assert.assertEquals(1, (actualGame.playoff!!.spot as Int).toLong())
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
    fun advanceByGame_roundSpotCalc() {
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val gameId = 1L
        val homeUser = EntityDefaults.user(gameId)
        val awayUser = EntityDefaults.user(2L)

        mockCountByTournament(MockitoUtils.anyObject())
        mockNumberOfGroupMembersAdvancing()

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer { invocation -> assertRoundSpot(invocation, 2, 3, tournament) } // Coming from round 1 and spot 6.
                .thenAnswer { invocation -> assertRoundSpot(invocation, 4, 1, tournament) } // Coming from round 3 and spot 1.
                .thenAnswer { invocation -> assertRoundSpot(invocation, 3, 1, tournament) } // Coming from round 2 and spot 2.
                .thenAnswer { invocation -> assertRoundSpot(invocation, 3, 2, tournament) } // Coming from round 2 and spot 4.

        Mockito
                .`when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument(0) }

        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(1, 6), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(3, 1), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 2), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 4), tournament))
    }

    private fun assertRoundSpot(invocation: InvocationOnMock, expectedRound: Int, expectedSpot: Int, tournament: Tournament): Any {
        val actualRound = invocation.getArgument<Int>(1)
        val actualSpot = invocation.getArgument<Int>(2)

        Assert.assertEquals(expectedRound.toLong(), actualRound.toLong())
        Assert.assertEquals(expectedSpot.toLong(), actualSpot.toLong())

        return Optional.of(Game(tournament = tournament))
    }

    @Test
    fun advanceByGame_advanceAsHomeOrAway() {
        val tournament = EntityDefaults.tournament()
        val gameId = 1L
        val homeUser = EntityDefaults.user(gameId)
        val awayUser = EntityDefaults.user(2L)

        mockCountByTournament(MockitoUtils.anyObject())
        mockNumberOfGroupMembersAdvancing()

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

        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 3, 1, createPlayoffGame(1, 3), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 4), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 3, 1, createPlayoffGame(3, 1), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(1, 8), tournament))
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 0, 3, createPlayoffGame(2, 1), tournament))
    }

    @Test
    fun advanceByGame_isFinalGame() {
        mockCountByTournament(MockitoUtils.anyObject())
        mockNumberOfGroupMembersAdvancing()

        Assertions
                .assertThat(playoffService.advanceByGame(
                        createGame(1L, EntityDefaults.user(1L), EntityDefaults.user(2L), 2, 3, createPlayoffGame(4, 1), EntityDefaults.tournament())))
                .isEmpty()
    }

    @Test
    fun onlyFinalGamesAreLeftToPlay() {
        mockNumberOfGroupMembersAdvancing()
        mockCountByTournament(MockitoUtils.anyObject())

        val findReadyGamesInRoundEqualOrGreaterThanAnswer = { _: InvocationOnMock, listSize: Int ->
            val list = Mockito.mock(List::class.java)
            Mockito.`when`(list.size).thenReturn(listSize)
            list
        }

        Mockito
                .`when`(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(4))
                .thenAnswer { invocation -> findReadyGamesInRoundEqualOrGreaterThanAnswer(invocation, 2) }
                .thenAnswer { invocation -> findReadyGamesInRoundEqualOrGreaterThanAnswer(invocation, 0) }

        Mockito
                .`when`(tournamentService.getCurrentTournament())
                .thenAnswer { EntityDefaults.tournament(status = TournamentStatus.GROUP) }
                .thenAnswer { EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS) }

        Assert.assertFalse(playoffService.onlyFinalGamesAreLeftToPlay())
        Assert.assertTrue(playoffService.onlyFinalGamesAreLeftToPlay())
        Assert.assertFalse(playoffService.onlyFinalGamesAreLeftToPlay())
    }

    private fun mockNumberOfGroupMembersAdvancing() {
        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING))
                .thenAnswer {
                    Configuration(
                            value = "2",
                            key = ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING,
                            author = EntityDefaults.user()
                    )
                }
    }

    private fun mockCountByTournament(tournament: Tournament) {
        Mockito
                .`when`(groupRepository.countByTournament(tournament))
                .thenReturn(8)
    }

    private fun createGame(id: Long?,
                           homeUser: User?, awayUser: User,
                           scoreHome: Int?, scoreAway: Int?,
                           playoffGame: PlayoffGame, tournament: Tournament): Game {
        val game = Game(tournament = tournament)
        game.id = id
        game.homeUser = homeUser
        game.awayUser = awayUser
        game.scoreHome = scoreHome
        game.scoreAway = scoreAway
        game.playoff = playoffGame
        return game
    }

    private fun createPlayoffGame(round: Int, spot: Int): PlayoffGame {
        val playoffGame = PlayoffGame()
        playoffGame.round = round
        playoffGame.spot = spot
        return playoffGame
    }
}


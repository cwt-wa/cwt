package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class GameServiceTest {

    @InjectMocks
    private lateinit var gameService: GameService

    @Mock
    private lateinit var configurationService: ConfigurationService

    @Mock
    private lateinit var tournamentService: TournamentService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var groupService: GroupService

    @Mock
    private lateinit var playoffService: PlayoffService

    @Test
    fun reportGameForGroupStage() {
        val homeUserId: Long = 1
        val awayUserId: Long = 3
        val tournament = EntityDefaults.tournament(status = TournamentStatus.GROUP)

        val awayUser = EntityDefaults.user(awayUserId)
        val homeUser = EntityDefaults.user(homeUserId)

        mockAndAssertValidationHappeningBeforeActualReport(homeUserId, awayUserId, tournament, awayUser, homeUser)

        val group = createGroup(tournament)

        Mockito
                .`when`(groupRepository.findByTournamentAndUser(tournament, awayUser))
                .thenReturn(group)

        val expectedScoreHome = 1
        val expectedScoreAway = 2

        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    assertIndependentOfTournamentStatus(
                            awayUser, homeUser, expectedScoreHome, expectedScoreAway, actualGame, tournament)
                    Assert.assertEquals(group, actualGame.group)
                    Assert.assertEquals(group.label, actualGame.group.label)
                    Assert.assertEquals(group.tournament, actualGame.group.tournament)
                    Assert.assertFalse(actualGame.isTechWin)
                    Assert.assertNull(actualGame.playoff)
                    Assert.assertNotNull(actualGame.group)

                    actualGame
                }

        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway)
    }

    @Test
    fun reportGameForPlayoffs() {
        val homeUserId: Long = 1
        val awayUserId: Long = 3
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)

        val awayUser = EntityDefaults.user(awayUserId)
        val homeUser = EntityDefaults.user(homeUserId)

        mockAndAssertValidationHappeningBeforeActualReport(homeUserId, awayUserId, tournament, awayUser, homeUser)

        val expectedScoreHome = 1
        val expectedScoreAway = 2

        Mockito
                .`when`(gameRepository.findNextPlayoffGameForUser(MockitoUtils.anyObject(), MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val game = Game()
                    game.tournament = invocation.getArgument(0)
                    game.homeUser = invocation.getArgument(1)
                    game.awayUser = awayUser
                    game.playoff = PlayoffGame()
                    game
                }
                .thenAnswer { invocation ->
                    val game = Game()
                    game.tournament = invocation.getArgument(0)
                    game.homeUser = awayUser
                    game.awayUser = invocation.getArgument(1)
                    game.playoff = PlayoffGame()
                    game
                }
                .thenAnswer { invocation ->
                    val game = Game()
                    game.tournament = invocation.getArgument(0)
                    game.homeUser = invocation.getArgument(1)
                    game.awayUser = EntityDefaults.user(19)
                    game.playoff = PlayoffGame()
                    game
                }


        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    assertIndependentOfTournamentStatus(
                            awayUser, homeUser, expectedScoreHome, expectedScoreAway, actualGame, tournament)
                    Assert.assertFalse(actualGame.isTechWin)
                    Assert.assertNull(actualGame.group)
                    Assert.assertNotNull(actualGame.playoff)

                    actualGame
                }
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    assertIndependentOfTournamentStatus(
                            homeUser, awayUser, expectedScoreAway, expectedScoreHome, actualGame, tournament)
                    Assert.assertFalse(actualGame.isTechWin)
                    Assert.assertNull(actualGame.group)
                    Assert.assertNotNull(actualGame.playoff)

                    actualGame
                }

        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway)
        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway)
        try {
            gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway)
            Assert.fail()
        } catch (ignored: GameService.InvalidOpponentException) {
        }

    }

    private fun assertIndependentOfTournamentStatus(
            awayUser: User, homeUser: User, expectedScoreHome: Int, expectedScoreAway: Int, actualGame: Game,
            expectedTournament: Tournament) {
        Assert.assertEquals(awayUser, actualGame.awayUser)
        Assert.assertEquals(homeUser, actualGame.homeUser)
        Assert.assertEquals(expectedScoreHome.toLong(), (actualGame.scoreHome as Int).toLong())
        Assert.assertEquals(expectedScoreAway.toLong(), (actualGame.scoreAway as Int).toLong())
        Assert.assertEquals(expectedScoreAway.toLong(), (actualGame.scoreAway as Int).toLong())
        Assert.assertEquals(expectedScoreAway.toLong(), (actualGame.scoreAway as Int).toLong())
        Assert.assertEquals(expectedTournament, actualGame.tournament)
    }

    private fun mockAndAssertValidationHappeningBeforeActualReport(
            homeUserId: Long, awayUserId: Long, tournament: Tournament, awayUser: User, homeUser: User) {
        Mockito
                .`when`(tournamentService.currentTournament)
                .thenReturn(tournament)

        Mockito
                .`when`(gameService.getBestOfValue(TournamentStatus.GROUP))
                .thenReturn(createGroupGameBestOfConfiguration(ConfigurationKey.GROUP_GAMES_BEST_OF))

        Mockito
                .`when`(gameService.getBestOfValue(TournamentStatus.PLAYOFFS))
                .thenReturn(createGroupGameBestOfConfiguration(ConfigurationKey.PLAYOFF_GAMES_BEST_OF))

        try {
            gameService.reportGame(homeUserId, awayUserId, 3, 1)
            Assert.fail()
        } catch (ignored: GameService.InvalidScoreException) {
        }

        try {
            gameService.reportGame(homeUserId, awayUserId, 0, 1)
            Assert.fail()
        } catch (ignored: GameService.InvalidScoreException) {
        }

        Mockito
                .`when`(userService.getRemainingOpponents(MockitoUtils.anyObject()))
                .thenReturn(listOf(EntityDefaults.user(99)))
                .thenReturn(listOf(awayUser))

        Mockito
                .`when`(userRepository.findById(homeUserId))
                .thenReturn(Optional.of(homeUser))

        Mockito
                .`when`(userRepository.findById(awayUserId))
                .thenReturn(Optional.of(awayUser))

        try {
            gameService.reportGame(homeUserId, awayUserId, 1, 2)
            Assert.fail()
        } catch (ignored: GameService.InvalidOpponentException) {
        }

    }

    private fun createGroup(tournament: Tournament): Group {
        val group = Group()
        group.id = 77L
        group.tournament = tournament
        group.label = GroupLabel.G
        return group
    }

    private fun createGroupGameBestOfConfiguration(configurationKey: ConfigurationKey): Configuration {
        val configuration = Configuration()
        configuration.key = configurationKey
        configuration.value = "3"
        return configuration
    }
}

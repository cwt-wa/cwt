package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.bet.service.BetRepository
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
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.schedule.service.ScheduleService
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class GameServiceTest {

    @InjectMocks
    @Spy
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

    @Mock
    private lateinit var ratingRepository: RatingRepository

    @Mock
    private lateinit var commentRepository: CommentRepository

    @Mock
    private lateinit var scheduleService: ScheduleService

    @Mock
    private lateinit var treeService: TreeService

    @Mock
    private lateinit var betRepository: BetRepository

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
                    Assert.assertEquals(group.label, actualGame.group!!.label)
                    Assert.assertEquals(group.tournament, actualGame.group!!.tournament)
                    Assert.assertFalse(actualGame.techWin)
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
                    val game = Game(tournament = invocation.getArgument(0))
                    game.homeUser = invocation.getArgument(1)
                    game.awayUser = awayUser
                    game.playoff = PlayoffGame(round = 1, spot = 1)
                    game
                }
                .thenAnswer { invocation ->
                    val game = Game(tournament = invocation.getArgument(0))
                    game.homeUser = awayUser
                    game.awayUser = invocation.getArgument(1)
                    game.playoff = PlayoffGame(round = 1, spot = 1)
                    game
                }
                .thenAnswer { invocation ->
                    val game = Game(tournament = invocation.getArgument(0))
                    game.homeUser = invocation.getArgument(1)
                    game.awayUser = EntityDefaults.user(19)
                    game.playoff = PlayoffGame(round = 1, spot = 1)
                    game
                }


        Mockito
                .`when`<Any>(gameRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    assertIndependentOfTournamentStatus(
                            awayUser, homeUser, expectedScoreHome, expectedScoreAway, actualGame, tournament)
                    Assert.assertFalse(actualGame.techWin)
                    Assert.assertNull(actualGame.group)
                    Assert.assertNotNull(actualGame.playoff)

                    actualGame
                }
                .thenAnswer { invocation ->
                    val actualGame = invocation.getArgument<Game>(0)

                    assertIndependentOfTournamentStatus(
                            homeUser, awayUser, expectedScoreAway, expectedScoreHome, actualGame, tournament)
                    Assert.assertFalse(actualGame.techWin)
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
                .`when`(tournamentService.getCurrentTournament())
                .thenReturn(tournament)

        Mockito
                .doReturn(createGroupGameBestOfConfiguration(ConfigurationKey.GROUP_GAMES_BEST_OF))
                .`when`(gameService).getBestOfValue(TournamentStatus.GROUP)

        Mockito
                .doReturn(createGroupGameBestOfConfiguration(ConfigurationKey.PLAYOFF_GAMES_BEST_OF))
                .`when`(gameService).getBestOfValue(TournamentStatus.PLAYOFFS)

        Assertions.assertThatThrownBy { gameService.reportGame(homeUserId, awayUserId, 3, 1) }
                .isExactlyInstanceOf(GameService.InvalidScoreException::class.java)

        Assertions.assertThatThrownBy { gameService.reportGame(homeUserId, awayUserId, 0, 1) }
                .isExactlyInstanceOf(GameService.InvalidScoreException::class.java)

        Mockito
                .`when`(userService.getRemainingOpponents(MockitoUtils.anyObject()))
                .thenReturn(listOf(EntityDefaults.user(99)))
                .thenReturn(listOf(EntityDefaults.user(99)))
                .thenReturn(listOf(awayUser))

        Mockito
                .`when`(userRepository.findById(homeUserId))
                .thenReturn(Optional.of(homeUser))

        Mockito
                .`when`(userRepository.findById(awayUserId))
                .thenReturn(Optional.of(awayUser))

        Assertions.assertThatThrownBy { gameService.reportGame(homeUserId, awayUserId, 2, 0) }
                .isExactlyInstanceOf(GameService.InvalidOpponentException::class.java)

        Assertions.assertThatThrownBy { gameService.reportGame(homeUserId, awayUserId, 1, 2) }
                .isExactlyInstanceOf(GameService.InvalidOpponentException::class.java)
    }

    @Test
    fun `place bet with existing bet`() {
        val game = EntityDefaults.game()
        val user = EntityDefaults.user()
        val bet = Bet(id = 1, betOnHome = true, game = game, user = user)

        Mockito
                .`when`(betRepository.findByUserAndGame(user, game))
                .thenReturn(Optional.of(bet))

        Mockito
                .`when`(betRepository.save(bet))
                .thenAnswer { it.arguments[0] }


        val actualPlacedBet = gameService.placeBet(game, user, false)

        Assertions.assertThat(actualPlacedBet).isEqualTo(bet)
        Assertions.assertThat(actualPlacedBet.betOnHome).isEqualTo(false)
    }

    @Test
    fun `place new bet`() {
        val game = EntityDefaults.game()
        val user = EntityDefaults.user()

        Mockito
                .`when`(betRepository.findByUserAndGame(user, game))
                .thenReturn(Optional.empty())

        Mockito
                .`when`(betRepository.save(MockitoUtils.anyObject<Bet>()))
                .thenAnswer { with(it.getArgument<Bet>(0)) { this.id = 42; this } }

        val actualPlacedBet = gameService.placeBet(game, user, false)

        Assertions.assertThat(actualPlacedBet.id).isEqualTo(42)
        Assertions.assertThat(actualPlacedBet.betOnHome).isEqualTo(false)
        Assertions.assertThat(actualPlacedBet.game).isEqualTo(game)
        Assertions.assertThat(actualPlacedBet.user).isEqualTo(user)
    }

    private fun createGroup(tournament: Tournament): Group {
        val group = Group()
        group.id = 77L
        group.tournament = tournament
        group.label = GroupLabel.G
        return group
    }

    private fun createGroupGameBestOfConfiguration(configurationKey: ConfigurationKey) =
            Configuration(
                    key = configurationKey,
                    value = "3",
                    author = EntityDefaults.user()
            )
}

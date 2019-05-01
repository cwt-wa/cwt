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
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceFinishTournamentTest {

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
    private lateinit var tournamentRepository: TournamentRepository

    private val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)

    @Test
    fun advanceByGame_isFinalGame() {
        initOneWayFinal()
        val (finalGame, thirdPlaceGame) = createOneWayFinalGames()

        Mockito
                .`when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, finalGame.playoff!!.round - 1))
                .thenReturn(listOf(thirdPlaceGame))

        playoffService.advanceByGame(finalGame)

        Mockito
                .verify(tournamentService)
                .finish(finalGame.winner(), finalGame.loser(), thirdPlaceGame.winner(), thirdPlaceGame.playoff!!.round)
    }

    @Test
    fun advanceByGame_isThirdPlaceGame() {
        initOneWayFinal()
        val (finalGame, thirdPlaceGame) = createOneWayFinalGames()

        Mockito
                .`when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, thirdPlaceGame.playoff!!.round + 1))
                .thenReturn(listOf(finalGame))

        playoffService.advanceByGame(thirdPlaceGame)

        Mockito
                .verify(tournamentService)
                .finish(finalGame.winner(), finalGame.loser(), thirdPlaceGame.winner(), thirdPlaceGame.playoff!!.round)
    }

    @Test
    fun advanceByGame_isThreeWayFinalGame() {
        initThreeWayFinal()

        val user1 = EntityDefaults.user(id = 1, username = "user1")
        val user2 = EntityDefaults.user(id = 2, username = "user2")
        val user3 = EntityDefaults.user(id = 3, username = "user3")

        val game = Game(
                id = 3,
                scoreHome = 4,
                scoreAway = 1,
                homeUser = user1,
                awayUser = user3,
                playoff = PlayoffGame(round = 2, spot = 2),
                tournament = tournament
        )

        Mockito
                .`when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, game.playoff!!.round))
                .thenReturn(listOf(
                        EntityDefaults.game(
                                id = 1,
                                homeUser = user1,
                                awayUser = user2,
                                scoreHome = 4,
                                scoreAway = 1,
                                playoff = PlayoffGame(round = game.playoff!!.round, spot = 1),
                                tournament = tournament),
                        EntityDefaults.game(
                                id = 2,
                                homeUser = user3,
                                awayUser = user2,
                                scoreHome = 4,
                                scoreAway = 1,
                                playoff = PlayoffGame(round = game.playoff!!.round, spot = 3),
                                tournament = tournament),
                        game.copy(scoreHome = null, scoreAway = null, reporter = null)
                ))

        playoffService.advanceByGame(game)

        Mockito.verify(tournamentService).finish(user1, user3, user2, game.playoff!!.round)
    }

    @Test
    fun advanceByGame_isTiedThreeWayFinalGame() {
        initThreeWayFinal()

        val user1 = EntityDefaults.user(id = 1, username = "user1")
        val user2 = EntityDefaults.user(id = 2, username = "user2")
        val user3 = EntityDefaults.user(id = 3, username = "user3")

        val game = Game(
                id = 2,
                scoreHome = 3,
                scoreAway = 1,
                homeUser = user3,
                awayUser = user1,
                playoff = PlayoffGame(round = 2, spot = 2),
                tournament = tournament
        )

        val threeWayFinalGames = listOf(
                EntityDefaults.game(
                        id = 1,
                        homeUser = user1,
                        awayUser = user2,
                        scoreHome = 4,
                        scoreAway = 1,
                        playoff = PlayoffGame(round = 2, spot = 1),
                        tournament = tournament),
                EntityDefaults.game(
                        id = 2,
                        homeUser = user3,
                        awayUser = user1,
                        scoreHome = 4,
                        scoreAway = 1,
                        playoff = PlayoffGame(round = 2, spot = 2),
                        tournament = tournament),
                EntityDefaults.game(
                        id = 3,
                        homeUser = user2,
                        awayUser = user3,
                        scoreHome = 4,
                        scoreAway = 1,
                        playoff = PlayoffGame(round = 2, spot = 3),
                        tournament = tournament)
        )

        Mockito
                .`when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, game.playoff!!.round))
                .thenReturn(threeWayFinalGames)

        Mockito
                .`when`(gameRepository.saveAll(MockitoUtils.anyObject<MutableIterable<Game>>()))
                .thenAnswer { answer ->
                    val games = answer.getArgument<MutableIterable<Game>>(0)
                    Assertions.assertThat(games).containsExactlyInAnyOrder(*threeWayFinalGames.toTypedArray())
                    Assertions.assertThat(games.map { it.voided }.any { !it }).isFalse()
                    games
                }
                .thenAnswer { answer ->
                    val games = answer.getArgument<MutableIterable<Game>>(0)
                    Assertions.assertThat(games.map { it.wasPlayed() }.any { it }).isFalse()
                    Assertions.assertThat(games.count { it.pairingInvolves(user1) }).isEqualTo(2)
                    Assertions.assertThat(games.count { it.pairingInvolves(user2) }).isEqualTo(2)
                    Assertions.assertThat(games.count { it.pairingInvolves(user3) }).isEqualTo(2)
                    Assertions.assertThat(games.any { it.homeUser == it.awayUser }).isFalse()
                    games
                }

        playoffService.advanceByGame(game)
    }

    private fun initThreeWayFinal() {
        Mockito
                .`when`(groupRepository.countByTournament(MockitoUtils.anyObject<Tournament>()))
                .thenReturn(3)

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING))
                .thenReturn(Configuration(
                        key = ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING,
                        value = "2",
                        author = EntityDefaults.user()
                ))

        Mockito
                .`when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, 1))
                .thenReturn(Mockito.spy<ArrayList<Game>>(object : ArrayList<Game>() {
                    override val size: Int
                        get() = 3
                }))
    }

    private fun initOneWayFinal() {
        Mockito
                .`when`(groupRepository.countByTournament(MockitoUtils.anyObject<Tournament>()))
                .thenReturn(8)

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING))
                .thenReturn(Configuration(
                        key = ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING,
                        value = "2",
                        author = EntityDefaults.user()
                ))
    }

    private fun createOneWayFinalGames(): Pair<Game, Game> {
        val finalGame = EntityDefaults.game(
                homeUser = EntityDefaults.user(id = 1, username = "finalGameWinner"),
                awayUser = EntityDefaults.user(id = 2, username = "finalGameLoser"),
                scoreHome = 4,
                scoreAway = 1,
                playoff = PlayoffGame(round = 5, spot = 1),
                tournament = tournament)

        val thirdPlaceGame = finalGame.copy(
                homeUser = EntityDefaults.user(id = 3, username = "thirdPlaceGameLoser"),
                awayUser = EntityDefaults.user(id = 4, username = "thirdPlaceGameWinner"),
                scoreHome = 2,
                scoreAway = 4,
                playoff = PlayoffGame(round = finalGame.playoff!!.round - 1, spot = 1))

        return Pair(finalGame, thirdPlaceGame)
    }

}
